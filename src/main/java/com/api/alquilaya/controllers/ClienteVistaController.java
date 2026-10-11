package com.api.alquilaya.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.api.alquilaya.models.Cliente;
import com.api.alquilaya.services.ClienteService;

@Controller
@RequestMapping("/vista/clientes")
public class ClienteVistaController extends VistaBase {

    private static final String RUTA = "/vista/clientes";

    private final ClienteService clienteService;

    public ClienteVistaController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String mostrar(Model model) {
        model.addAttribute("clientes", clienteService.listar());
        return "clientes";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Cliente cliente, RedirectAttributes atributos) {
        return ejecutar(RUTA, atributos, "Cliente registrado correctamente.", () -> clienteService.crear(cliente));
    }

    @PostMapping("/{id}/actualizar")
    public String actualizar(@PathVariable Long id, @ModelAttribute Cliente cliente, RedirectAttributes atributos) {
        return ejecutar(RUTA, atributos, "Cliente actualizado correctamente.",
                () -> clienteService.actualizar(id, cliente));
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes atributos) {
        return ejecutar(RUTA, atributos, "Cliente eliminado correctamente.", () -> clienteService.eliminar(id));
    }
}
