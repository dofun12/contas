package org.lemanoman.contas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/public/cadastro")
public class CadastroController {


    @Autowired
    private DatabaseService databaseService;


    @PostMapping("/postCadastro")
    public String foobarPost(
            @ModelAttribute("command") FormCadastro command,
            Model model ) {
            System.out.println(command.getNome());

            if(databaseService.createUser(command.getUsuario(),command.getNome(),command.getSenha())){
                model.addAttribute("type","info");
                model.addAttribute("message","Salvo com sucesso!");
            }else{
                model.addAttribute("type","error");
                model.addAttribute("message","Erro ao salvar!");
            }


            return "cadastro";
    }
}
