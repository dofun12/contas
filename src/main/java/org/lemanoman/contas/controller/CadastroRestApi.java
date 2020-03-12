package org.lemanoman.contas.controller;

import org.lemanoman.contas.dto.CadastroForm;
import org.lemanoman.contas.dto.UserModel;
import org.lemanoman.contas.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
public class CadastroRestApi {


    @Autowired
    private DatabaseService databaseService;


    @GetMapping(value = "/cadastro/verify/{user}", produces = "application/json")
    public String verificarUsuario(@PathVariable("user") String user) {
        if(user.length()>=10){
            return "false";
        }
        UserModel userModel = databaseService.getUsuario(user);
        if (userModel != null) {
            return "true";
        } else {
            return "false";
        }

    }
}
