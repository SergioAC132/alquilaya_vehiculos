package com.api.alquilaya.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.api.alquilaya.models.dto.AlquilerDto;
import com.api.alquilaya.services.AlquilerService;
import com.api.alquilaya.services.ClienteService;
import com.api.alquilaya.services.VehiculoService;

@Controller
@RequestMapping("/vista/alquileres")
public class AlquilerVistaController extends VistaBase {

    private static final String RUTA = "/vista/alquileres";

    private final AlquilerService alquilerService;
    private final VehiculoService vehiculoService;
    private final ClienteService clienteService;

    public AlquilerVistaController(AlquilerService alquilerService, VehiculoService vehiculoService,
            ClienteService clienteService) {
        this.alquilerService = alquilerService;
        this.vehiculoService = vehiculoService;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String mostrar(Model model) {
        model.addAttribute("alquileres", alquilerService.listar());
        model.addAttribute("vehiculos", vehiculoService.listarDisponibles());
        model.addAttribute("clientes", clienteService.listar());
        return "alquileres";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute AlquilerDto alquiler, RedirectAttributes atributos) {
        return ejecutar(RUTA, atributos, "Alquiler registrado correctamente.", () -> alquilerService.crear(alquiler));
    }

    @PostMapping("/{id}/actualizar")
    public String actualizar(@PathVariable Long id, @ModelAttribute AlquilerDto alquiler,
            RedirectAttributes atributos) {
        return ejecutar(RUTA, atributos, "Fechas actualizadas y costo recalculado.",
                () -> alquilerService.actualizar(id, alquiler));
    }

    @PostMapping("/{id}/finalizar")
    public String finalizar(@PathVariable Long id, RedirectAttributes atributos) {
        return ejecutar(RUTA, atributos, "Vehículo devuelto. Ya está disponible.",
                () -> alquilerService.finalizar(id));
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes atributos) {
        return ejecutar(RUTA, atributos, "Alquiler eliminado correctamente.", () -> alquilerService.eliminar(id));
    }
}
