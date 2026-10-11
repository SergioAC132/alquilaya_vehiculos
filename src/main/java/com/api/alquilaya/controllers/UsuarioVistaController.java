package com.api.alquilaya.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.api.alquilaya.models.dto.UsuarioDto;
import com.api.alquilaya.services.UsuarioService;

@Controller
@RequestMapping("/vista/usuarios")
public class UsuarioVistaController extends VistaBase {

    private static final String RUTA = "/vista/usuarios";

    private final UsuarioService usuarioService;

    public UsuarioVistaController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String mostrar(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "usuarios";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute UsuarioDto usuario, RedirectAttributes atributos) {
        return ejecutar(RUTA, atributos, "Usuario registrado correctamente.", () -> usuarioService.crear(usuario));
    }

    @PostMapping("/{id}/actualizar")
    public String actualizar(@PathVariable Long id, @ModelAttribute UsuarioDto usuario,
            RedirectAttributes atributos) {
        return ejecutar(RUTA, atributos, "Usuario actualizado correctamente.",
                () -> usuarioService.actualizar(id, usuario));
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes atributos) {
        return ejecutar(RUTA, atributos, "Usuario eliminado correctamente.", () -> usuarioService.eliminar(id));
    }
}
