package com.bancodobrasil.residencia.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
            "projeto", "Residência Backend",
            "mensagem", "Deploy realizado com sucesso!",
            "status", "UP",
            "ambiente", "Render",
            "tecnologia", "Spring Boot"
        );
    }
}
