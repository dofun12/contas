package org.lemanoman.contas.controller;

import org.lemanoman.contas.dto.Conta;
import org.lemanoman.contas.service.DatabaseService;
import org.lemanoman.contas.dto.FormNovaConta;
import org.lemanoman.contas.dto.UserModel;
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
    public String listar(Model model, Principal principal){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
        }
        UserModel userModel = databaseService.getUsuario(principal.getName());
        List<Conta> contas =  databaseService.getListContasMesAtual(principal.getName());
        Double total = 0d;
        for(Conta conta: contas){
            total = total+conta.getTotal();
        }
        model.addAttribute("contas",contas);
        model.addAttribute("total",total);
        model.addAttribute("name",userModel.getNome());
        return "/contas/listar";
    }

    @GetMapping("/contas/nova")
    public String novaConta(Model model){
        FormNovaConta formNovaConta = new FormNovaConta();
        formNovaConta.setData(simpleDateFormat.format(new Date()));
        formNovaConta.setPago(true);
        model.addAttribute("post",formNovaConta);
        return "/contas/editar";
    }

    @GetMapping("/contas/editar/{lancamento}/{ano}/{mes}/{dia}")
    public String editarConta(
            @PathVariable("lancamento") String lancamento,
            @PathVariable("ano") Integer ano,
            @PathVariable("mes") Integer mes,
            @PathVariable("dia") Integer dia,
            Model model,
            Principal principal){
        Conta conta = databaseService.getConta(principal.getName(),lancamento,ano,mes,dia);
        FormNovaConta formNovaConta = new FormNovaConta();
        if(conta!=null){
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH,dia);
            calendar.set(Calendar.MONTH,mes+1);
            calendar.set(Calendar.YEAR, ano);
            formNovaConta.setLancamento(conta.getLancamento());
            formNovaConta.setDescricao(conta.getDescricao());
            formNovaConta.setPago(conta.getPago());
            formNovaConta.setTotal(conta.getTotal());
            formNovaConta.setData(simpleDateFormat.format(calendar.getTime()));
            formNovaConta.setPago(true);
            model.addAttribute("post",formNovaConta);
            return "/contas/editar";
        }else{
            return "";
        }

    }

    @PostMapping("/contas/nova")
    public String postNovaConta(@ModelAttribute("command") FormNovaConta command,Model model, Principal principal ){
        try{

            Date date = simpleDateFormat.parse(command.getData());
            if(databaseService.addConta(command.getLancamento(),command.getDescricao(),principal.getName(),command.getTotal(),date,command.getPago())){
                model.addAttribute("type","info");
                model.addAttribute("message","Salvo com sucesso!");
                model.addAttribute("post",command);
            }else{
                model.addAttribute("type","error");
                model.addAttribute("message","Erro ao salvar!");
            }
        }catch (Exception ex){
            model.addAttribute("type","error");
            model.addAttribute("message",ex.getMessage());
            ex.printStackTrace();
        }
        model.addAttribute("post",command);
        return "/contas/editar";
    }


}
