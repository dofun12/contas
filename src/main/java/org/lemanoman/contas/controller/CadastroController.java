package org.lemanoman.contas.controller;

import org.lemanoman.contas.service.DatabaseService;
import org.lemanoman.contas.dto.CadastroForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/public/cadastro")
public class CadastroController {


    @Autowired
    private DatabaseService databaseService;


    @PostMapping("/postCadastro")
    public String foobarPost(
            @ModelAttribute("command") CadastroForm command,
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
