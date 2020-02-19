package org.lemanoman.contas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

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
            @PathVariable("ano") String ano,
            @PathVariable("mes") String mes,
            @PathVariable("dia") String dia, Model model){
        FormNovaConta formNovaConta = new FormNovaConta();
        formNovaConta.setData(simpleDateFormat.format(new Date()));
        formNovaConta.setPago(true);
        model.addAttribute("post",formNovaConta);
        return "/contas/editar";
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
