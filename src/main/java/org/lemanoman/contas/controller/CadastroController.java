package org.lemanoman.contas.controller;

import javafx.application.Application;
import org.lemanoman.contas.dto.UserModel;
import org.lemanoman.contas.service.DatabaseService;
import org.lemanoman.contas.dto.CadastroForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CadastroController {


    @Autowired
    private DatabaseService databaseService;

    @GetMapping("/cadastro")
    public String cadastro(
            @ModelAttribute("command") CadastroForm command,
            Model model) {
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String foobarPost(
            @ModelAttribute("command") CadastroForm command,
            Model model) {
        System.out.println(command.getNome());

        if (databaseService.createUser(command.getUsuario(), command.getNome(), command.getSenha())) {
            model.addAttribute("type", "info");
            model.addAttribute("message", "Login criado com sucesso!");
        } else {
            model.addAttribute("type", "error");
            model.addAttribute("message", "Erro ao criar o login!");
        }


        return "login";
    }
}
