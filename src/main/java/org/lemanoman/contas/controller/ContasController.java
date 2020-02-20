package org.lemanoman.contas.controller;

import org.lemanoman.contas.dto.Conta;
import org.lemanoman.contas.dto.TimePeriod;
import org.lemanoman.contas.service.DatabaseService;
import org.lemanoman.contas.dto.NovaContaForm;
import org.lemanoman.contas.dto.UserModel;
import org.lemanoman.contas.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class ContasController {

    @Autowired
    private DatabaseService databaseService;

    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @GetMapping("/contas/listar")
    public String listar(Model model, Principal principal) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
        }
        UserModel userModel = databaseService.getUsuario(principal.getName());
        List<Conta> contas = databaseService.getListContasMesAtual(principal.getName());
        Double total = 0d;
        for (Conta conta : contas) {
            total = total + conta.getTotal();
        }
        model.addAttribute("contas", contas);
        model.addAttribute("total", total);
        model.addAttribute("name", userModel.getNome());
        return "/contas/listar";
    }

    @GetMapping("/contas/listar/{ano}/{mes}")
    public String listar(
            @PathVariable("ano") Integer ano,
            @PathVariable("mes") Integer mes,Model model, Principal principal) {
        UserModel userModel = databaseService.getUsuario(principal.getName());
        List<Conta> contas = databaseService.getListContas(principal.getName(),ano,mes);
        Double total = 0d;
        for (Conta conta : contas) {
            total = total + conta.getTotal();
        }
        model.addAttribute("contas", contas);
        model.addAttribute("total", total);
        model.addAttribute("name", userModel.getNome());
        return "/contas/listar";
    }

    @GetMapping("/contas/nova")
    public String novaConta(Model model) {
        NovaContaForm novaContaForm = new NovaContaForm();
        novaContaForm.setData(simpleDateFormat.format(new Date()));
        novaContaForm.setEditar(false);
        novaContaForm.setPago(false);
        model.addAttribute("post", novaContaForm);
        return "/contas/editar";
    }

    private String doEdit(String lancamento, Integer ano, Integer mes, Integer dia, Model model, Principal principal) {
        Calendar calIni = Calendar.getInstance();
        calIni.set(Calendar.MONTH,mes-1);
        calIni.set(Calendar.DAY_OF_MONTH, calIni.getActualMinimum(Calendar.DAY_OF_MONTH));
        calIni.set(Calendar.YEAR,ano);

        Calendar calEnd = Calendar.getInstance();
        calEnd.set(Calendar.MONTH,mes-1);
        calEnd.set(Calendar.DAY_OF_MONTH, calIni.getActualMaximum(Calendar.DAY_OF_MONTH));
        calEnd.set(Calendar.YEAR,ano);



        Conta conta = databaseService.getConta(principal.getName(), lancamento, ano, mes);
        NovaContaForm novaContaForm = new NovaContaForm();
        if (conta != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, dia);
            calendar.set(Calendar.MONTH, mes - 1);
            calendar.set(Calendar.YEAR, ano);
            novaContaForm.setMinDate(TimeUtils.YEAR_FORMATTER.format(calIni.getTime()));
            novaContaForm.setMaxDate(TimeUtils.YEAR_FORMATTER.format(calEnd.getTime()));
            novaContaForm.setEditar(true);
            novaContaForm.setLancamento(conta.getLancamento());
            novaContaForm.setDescricao(conta.getDescricao());
            novaContaForm.setPago(conta.getPago());
            novaContaForm.setTotal(conta.getTotal());
            novaContaForm.setData(simpleDateFormat.format(calendar.getTime()));
            novaContaForm.setPago(conta.getPago());
            model.addAttribute("post", novaContaForm);
            return "/contas/editar";
        } else {
            return "";
        }
    }

    @GetMapping("/contas/editar/{lancamento}/{ano}/{mes}/{dia}")
    public String editarConta(
            @PathVariable("lancamento") String lancamento,
            @PathVariable("ano") Integer ano,
            @PathVariable("mes") Integer mes,
            @PathVariable("dia") Integer dia,
            Model model,
            Principal principal) {
        return doEdit(lancamento, ano, mes, dia, model, principal);
    }

    @GetMapping("/contas/editar/{lancamento}/{dateStr}")
    public String editarContaSimples(
            @PathVariable("lancamento") String lancamento,
            @PathVariable("dateStr") String dateStr,
            Model model,
            Principal principal) {

        TimePeriod tp = TimeUtils.toTimePeriod(dateStr);
        if (tp != null) {
            return doEdit(lancamento, tp.getAno(), tp.getMes(), tp.getDia(), model, principal);
        } else {
            return "";
        }

    }

    private void addError(Model model) {
        addMessage("Erro ao salvar!", "error", model);
    }

    private void addSucesso(Model model) {
        addMessage("Salvo com sucesso!", "info", model);
    }

    private void addMessage(String message, String tipo, Model model) {
        model.addAttribute("type", tipo);
        model.addAttribute("message", message);
    }

    @PostMapping("/contas/nova")
    public String postNovaConta(@ModelAttribute("command") NovaContaForm command, Model model, Principal principal) {
        try {
            TimePeriod tp = TimeUtils.toTimePeriod(command.getData());
            Date date = simpleDateFormat.parse(command.getData());
            if (tp != null && databaseService.getConta(principal.getName(), command.getLancamento(), tp.getAno(), tp.getMes()) != null) {
                if (databaseService.editarConta(command.getLancamento(), command.getDescricao(), principal.getName(), command.getTotal(), date, command.getPago())) {
                    addSucesso(model);
                    model.addAttribute("post", command);
                } else {
                    addError(model);
                }
            } else {
                if (databaseService.addConta(command.getLancamento(), command.getDescricao(), principal.getName(), command.getTotal(), date, command.getPago())) {
                    addSucesso(model);
                    model.addAttribute("post", command);
                } else {
                    addError(model);
                }
            }

        } catch (Exception ex) {
            model.addAttribute("type", "error");
            model.addAttribute("message", ex.getMessage());
            ex.printStackTrace();
        }
        model.addAttribute("post", command);
        return "/contas/editar";
    }


}
