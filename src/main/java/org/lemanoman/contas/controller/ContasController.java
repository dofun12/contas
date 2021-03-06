package org.lemanoman.contas.controller;

import org.lemanoman.contas.dto.*;
import org.lemanoman.contas.service.DatabaseService;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        model.addAttribute("anos", getAnos(null));
        model.addAttribute("meses",getMeses(null));
        return listar(model,principal, null);
    }

    private String listar(Model model, Principal principal,List<Conta> contas){
        UserModel userModel = databaseService.getUsuario(principal.getName());
        if(contas==null){
            contas = databaseService.getListContasMesAtual(principal.getName());
        }
        Double total = 0d;
        for (Conta conta : contas) {
            total = total + conta.getTotal();
            conta.setTotalFormatado("R$ "+formatPrice(conta.getTotal()));
        }
        model.addAttribute("contas", contas);
        model.addAttribute("total", "R$ "+formatPrice(total));
        model.addAttribute("name", userModel.getNome());
        return "contas/listar";
    }

    public static String formatPrice(double value) {
        DecimalFormat formatter;
        if (value<=99999)
            formatter = new DecimalFormat("###,###,##0.00");
        else
            formatter = new DecimalFormat("#,##,##,###.00");

        return formatter.format(value);
    }

    @GetMapping("/contas/listar/{ano}/{mes}")
    public String listar(
            @PathVariable("ano") Integer ano,
            @PathVariable("mes") Integer mes,Model model, Principal principal) {
        List<Conta> contas = databaseService.getListContas(principal.getName(),ano,mes);
        model.addAttribute("anos", getAnos(ano));
        model.addAttribute("meses",getMeses(mes));
        return listar(model,principal,contas);
    }

    @GetMapping("/contas/nova")
    public String novaConta(Model model) {
        NovaContaForm novaContaForm = new NovaContaForm();
        novaContaForm.setData(simpleDateFormat.format(new Date()));
        novaContaForm.setEditar(false);
        novaContaForm.setPago(false);
        model.addAttribute("post", novaContaForm);
        return "contas/editar";
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
            return "contas/editar";
        } else {
            return "";
        }
    }


    @GetMapping("/contas/deletar/{lancamento}/{ano}/{mes}/{dia}")
    public String deletar(
            @PathVariable("lancamento") String lancamento,
            @PathVariable("ano") Integer ano,
            @PathVariable("mes") Integer mes,
            @PathVariable("dia") Integer dia,
            Model model,
            Principal principal) {
        databaseService.deleteConta(lancamento,principal.getName(),ano,mes,dia);
        List<Conta> contas = databaseService.getListContas(principal.getName(),ano,mes);
        model.addAttribute("anos", getAnos(ano));
        model.addAttribute("meses",getMeses(mes));
        return listar(model,principal, contas);
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

    private List<Ano> getAnos(Integer ano){
        List<Ano> anos = new ArrayList<>();
        Integer anoAtual = ano;
        if(anoAtual==null){
            anoAtual = Calendar.getInstance().get(Calendar.YEAR);
        }
        anos.add(new Ano(anoAtual,true));

        for(Integer anoDB:databaseService.getListAnos()){
            if(!anoDB.equals(anoAtual)){
                anos.add(new Ano(anoDB,false));
            }

        }
        return anos;
    }

    private List<Mes> getMeses(Integer mesAtual){
        Calendar calendar = Calendar.getInstance();
        if(mesAtual==null){
            mesAtual = (calendar.get(Calendar.MONTH));
        }else{
            mesAtual = mesAtual-1;
        }

        int minMonth = calendar.getActualMinimum(Calendar.MONTH);
        int maxMonth = calendar.getActualMaximum(Calendar.MONTH);
        calendar.set(Calendar.MONTH, minMonth);

        SimpleDateFormat monthNameFormat = new SimpleDateFormat("MMMM");
        List<Mes> meses = new ArrayList<>();
        int i = minMonth;
        while (i<=maxMonth){
            meses.add(new Mes(i+1,monthNameFormat.format(calendar.getTime()),mesAtual.equals(i)));
            calendar.add(Calendar.MONTH,1);
            i++;
        }
        return meses;
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
        return "contas/editar";
    }

    @PostMapping("/contas/copiarProximoMes")
    public String copiarProximoMes(@ModelAttribute("command")CopiarForm copiarForm, Model model, Principal principal) {
        try {
            copiarForm.getAno();
            copiarForm.getMes();
            List<Conta> contas = databaseService.getListContas(principal.getName(), copiarForm.getAno(),copiarForm.getMes());
            for(Conta conta: contas){
                databaseService.addConta(conta.getLancamento(),conta.getDescricao(),conta.getUsuario(),conta.getTotal(),TimeUtils.toDate(conta.getDia(),conta.getMes()+1,conta.getAno()),false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        List<Conta> contas = databaseService.getListContas(principal.getName(),copiarForm.getAno(),copiarForm.getMes());
        model.addAttribute("anos", getAnos(copiarForm.getAno()));
        model.addAttribute("meses",getMeses(copiarForm.getMes()));
        return listar(model,principal,contas);
    }


}
