package com.api.alquilaya.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.alquilaya.models.Oficina;
import com.api.alquilaya.repositories.IOficinaRepository;
import com.api.alquilaya.utils.Validar;

@Service
@Transactional
public class OficinaService {

    private final IOficinaRepository oficinaRepository;

    public OficinaService(IOficinaRepository oficinaRepository) {
        this.oficinaRepository = oficinaRepository;
    }

    @Transactional(readOnly = true)
    public List<Oficina> listar() {
        return oficinaRepository.findAll(Sort.by("idOficina"));
    }

    @Transactional(readOnly = true)
    public Oficina obtener(Long id) {
        return oficinaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Oficina no encontrada: " + id));
    }

    public Oficina crear(Oficina datos) {
        Oficina oficina = new Oficina();
        aplicar(oficina, datos);
        return oficinaRepository.save(oficina);
    }

    public Oficina actualizar(Long id, Oficina datos) {
        Oficina oficina = obtener(id);
        aplicar(oficina, datos);
        return oficinaRepository.save(oficina);
    }

    public void eliminar(Long id) {
        obtener(id);
        try {
            oficinaRepository.deleteById(id);
            oficinaRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar la oficina porque tiene vehículos asignados.");
        }
    }

    private void aplicar(Oficina destino, Oficina datos) {
        destino.setNombre(Validar.texto(datos.getNombre(), "El nombre"));
        destino.setDireccion(Validar.texto(datos.getDireccion(), "La dirección"));
        destino.setCiudad(Validar.texto(datos.getCiudad(), "La ciudad"));
        destino.setRegion(Validar.texto(datos.getRegion(), "La región"));
    }
}
