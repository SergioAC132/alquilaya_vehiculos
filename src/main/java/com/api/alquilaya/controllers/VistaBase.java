package com.api.alquilaya.controllers;

import java.util.NoSuchElementException;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Evita repetir el try/catch + flash attributes en cada controlador de vista
public abstract class VistaBase {

    protected String ejecutar(String destino, RedirectAttributes atributos, String mensajeOk, Runnable accion) {
        try {
            accion.run();
            atributos.addFlashAttribute("mensaje", mensajeOk);
        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException exception) {
            atributos.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:" + destino;
    }
}
