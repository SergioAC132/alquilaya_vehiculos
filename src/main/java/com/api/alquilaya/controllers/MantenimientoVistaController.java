package com.api.alquilaya.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.api.alquilaya.models.dto.MantenimientoDto;
import com.api.alquilaya.services.MantenimientoService;
import com.api.alquilaya.services.VehiculoService;

@Controller
@RequestMapping("/vista/mantenimientos")
public class MantenimientoVistaController extends VistaBase {

    private static final String RUTA = "/vista/mantenimientos";

    private final MantenimientoService mantenimientoService;
    private final VehiculoService vehiculoService;

    public MantenimientoVistaController(MantenimientoService mantenimientoService,
            VehiculoService vehiculoService) {
        this.mantenimientoService = mantenimientoService;
        this.vehiculoService = vehiculoService;
    }

    @GetMapping
    public String mostrar(Model model) {
        model.addAttribute("mantenimientos", mantenimientoService.listar());
        model.addAttribute("vehiculos", vehiculoService.listarDisponibles());
        return "mantenimientos";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute MantenimientoDto mantenimiento, RedirectAttributes atributos) {
        return ejecutar(RUTA, atributos, "Mantenimiento registrado correctamente.",
                () -> mantenimientoService.crear(mantenimiento));
    }

    @PostMapping("/{id}/actualizar")
    public String actualizar(@PathVariable Long id, @ModelAttribute MantenimientoDto mantenimiento,
            RedirectAttributes atributos) {
        return ejecutar(RUTA, atributos, "Mantenimiento actualizado correctamente.",
                () -> mantenimientoService.actualizar(id, mantenimiento));
    }

    @PostMapping("/{id}/finalizar")
    public String finalizar(@PathVariable Long id, RedirectAttributes atributos) {
        return ejecutar(RUTA, atributos, "Mantenimiento terminado. El vehículo ya está disponible.",
                () -> mantenimientoService.finalizar(id));
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes atributos) {
        return ejecutar(RUTA, atributos, "Mantenimiento eliminado correctamente.",
                () -> mantenimientoService.eliminar(id));
    }
}