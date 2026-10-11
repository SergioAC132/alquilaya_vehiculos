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

import com.api.alquilaya.models.dto.AlquilerDto;
import com.api.alquilaya.services.AlquilerService;

@RestController
@RequestMapping("/api/alquileres")
public class AlquilerController {

    private final AlquilerService alquilerService;

    public AlquilerController(AlquilerService alquilerService) {
        this.alquilerService = alquilerService;
    }

    @GetMapping
    public List<AlquilerDto> listar() {
        return alquilerService.listar();
    }

    @GetMapping("/{id}")
    public AlquilerDto obtener(@PathVariable Long id) {
        return alquilerService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<AlquilerDto> crear(@RequestBody AlquilerDto alquiler) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alquilerService.crear(alquiler));
    }

    @PutMapping("/{id}")
    public AlquilerDto actualizar(@PathVariable Long id, @RequestBody AlquilerDto alquiler) {
        return alquilerService.actualizar(id, alquiler);
    }

    @PutMapping("/{id}/finalizar")
    public AlquilerDto finalizar(@PathVariable Long id) {
        return alquilerService.finalizar(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        alquilerService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
