package com.api.alquilaya.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.api.alquilaya.models.dto.VehiculoDto;
import com.api.alquilaya.services.OficinaService;
import com.api.alquilaya.services.VehiculoService;

@Controller
@RequestMapping("/vista/vehiculos")
public class VehiculoVistaController extends VistaBase {

    private static final String RUTA = "/vista/vehiculos";

    private final VehiculoService vehiculoService;
    private final OficinaService oficinaService;

    public VehiculoVistaController(VehiculoService vehiculoService, OficinaService oficinaService) {
        this.vehiculoService = vehiculoService;
        this.oficinaService = oficinaService;
    }

    @GetMapping
    public String mostrar(Model model) {
        model.addAttribute("vehiculos", vehiculoService.listar());
        model.addAttribute("oficinas", oficinaService.listar());
        return "vehiculos";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute VehiculoDto vehiculo, RedirectAttributes atributos) {
        return ejecutar(RUTA, atributos, "Vehículo registrado correctamente.", () -> vehiculoService.crear(vehiculo));
    }

    @PostMapping("/{id}/actualizar")
    public String actualizar(@PathVariable Long id, @ModelAttribute VehiculoDto vehiculo,
            RedirectAttributes atributos) {
        return ejecutar(RUTA, atributos, "Vehículo actualizado correctamente.",
                () -> vehiculoService.actualizar(id, vehiculo));
    }

    @PostMapping("/{id}/cargar-bateria")
    public String cargarBateria(@PathVariable Long id, RedirectAttributes atributos) {
        return ejecutar(RUTA, atributos, "Batería cargada al 100%.", () -> vehiculoService.cargarBateria(id));
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes atributos) {
        return ejecutar(RUTA, atributos, "Vehículo eliminado correctamente.", () -> vehiculoService.eliminar(id));
    }
}
