package com.api.alquilaya.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    // Un solo mapeo a "/" en todo el proyecto
    @GetMapping("/")
    public String inicio() {
        return "redirect:/vista/alquileres";
    }
}
