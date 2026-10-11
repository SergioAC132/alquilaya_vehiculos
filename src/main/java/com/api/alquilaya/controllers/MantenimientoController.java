package com.api.alquilaya.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.alquilaya.models.dto.MantenimientoDto;
import com.api.alquilaya.services.MantenimientoService;

@RestController
@RequestMapping("/api/mantenimientos")
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;

    public MantenimientoController(MantenimientoService mantenimientoService) {
        this.mantenimientoService = mantenimientoService;
    }

    @GetMapping
    public List<MantenimientoDto> listar() {
        return mantenimientoService.listar();
    }

    @GetMapping("/{id}")
    public MantenimientoDto obtener(@PathVariable Long id) {
        return mantenimientoService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<MantenimientoDto> crear(@RequestBody MantenimientoDto mantenimiento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mantenimientoService.crear(mantenimiento));
    }

    @PutMapping("/{id}")
    public MantenimientoDto actualizar(@PathVariable Long id, @RequestBody MantenimientoDto mantenimiento) {
        return mantenimientoService.actualizar(id, mantenimiento);
    }

    @PutMapping("/{id}/finalizar")
    public MantenimientoDto finalizar(@PathVariable Long id) {
        return mantenimientoService.finalizar(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mantenimientoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}