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

import com.api.alquilaya.models.Oficina;
import com.api.alquilaya.services.OficinaService;

@RestController
@RequestMapping("/api/oficinas")
public class OficinaController {

    private final OficinaService oficinaService;

    public OficinaController(OficinaService oficinaService) {
        this.oficinaService = oficinaService;
    }

    @GetMapping
    public List<Oficina> listar() {
        return oficinaService.listar();
    }

    @GetMapping("/{id}")
    public Oficina obtener(@PathVariable Long id) {
        return oficinaService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<Oficina> crear(@RequestBody Oficina oficina) {
        return ResponseEntity.status(HttpStatus.CREATED).body(oficinaService.crear(oficina));
    }

    @PutMapping("/{id}")
    public Oficina actualizar(@PathVariable Long id, @RequestBody Oficina oficina) {
        return oficinaService.actualizar(id, oficina);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        oficinaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
