package com.api.alquilaya.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.alquilaya.models.dto.VehiculoDto;
import com.api.alquilaya.services.VehiculoService;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @GetMapping
    public List<VehiculoDto> listar() {
        return vehiculoService.listar();
    }

    @GetMapping("/disponibles")
    public List<VehiculoDto> disponibles() {
        return vehiculoService.listarDisponibles();
    }

    @GetMapping("/{id}")
    public VehiculoDto obtener(@PathVariable Long id) {
        return vehiculoService.obtener(id);
    }

    // GET /api/vehiculos/3/precio?dias=5
    @GetMapping("/{id}/precio")
    public Map<String, Object> precio(@PathVariable Long id, @RequestParam int dias) {
        return Map.of("idVehiculo", id, "dias", dias, "precio", vehiculoService.calcularPrecio(id, dias));
    }

    @PostMapping
    public ResponseEntity<VehiculoDto> crear(@RequestBody VehiculoDto vehiculo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculoService.crear(vehiculo));
    }

    @PutMapping("/{id}")
    public VehiculoDto actualizar(@PathVariable Long id, @RequestBody VehiculoDto vehiculo) {
        return vehiculoService.actualizar(id, vehiculo);
    }

    @PutMapping("/{id}/cargar-bateria")
    public VehiculoDto cargarBateria(@PathVariable Long id) {
        return vehiculoService.cargarBateria(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        vehiculoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
