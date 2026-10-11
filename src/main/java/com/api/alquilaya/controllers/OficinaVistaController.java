package com.api.alquilaya.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.api.alquilaya.models.Oficina;
import com.api.alquilaya.services.OficinaService;

@Controller
@RequestMapping("/vista/oficinas")
public class OficinaVistaController extends VistaBase {

    private static final String RUTA = "/vista/oficinas";

    private final OficinaService oficinaService;

    public OficinaVistaController(OficinaService oficinaService) {
        this.oficinaService = oficinaService;
    }

    @GetMapping
    public String mostrar(Model model) {
        model.addAttribute("oficinas", oficinaService.listar());
        return "oficinas";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Oficina oficina, RedirectAttributes atributos) {
        return ejecutar(RUTA, atributos, "Oficina registrada correctamente.", () -> oficinaService.crear(oficina));
    }

    @PostMapping("/{id}/actualizar")
    public String actualizar(@PathVariable Long id, @ModelAttribute Oficina oficina, RedirectAttributes atributos) {
        return ejecutar(RUTA, atributos, "Oficina actualizada correctamente.",
                () -> oficinaService.actualizar(id, oficina));
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes atributos) {
        return ejecutar(RUTA, atributos, "Oficina eliminada correctamente.", () -> oficinaService.eliminar(id));
    }
}
