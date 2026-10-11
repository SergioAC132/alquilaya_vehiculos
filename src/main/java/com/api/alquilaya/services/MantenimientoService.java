package com.api.alquilaya.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.alquilaya.models.dto.MantenimientoDto;
import com.api.alquilaya.models.Mantenimiento;
import com.api.alquilaya.models.Vehiculo;
import com.api.alquilaya.models.Vehiculo.EstadoVehiculo;
import com.api.alquilaya.repositories.IMantenimientoRepository;
import com.api.alquilaya.repositories.IVehiculoRepository;
import com.api.alquilaya.utils.Validar;

@Service
@Transactional
public class MantenimientoService {

    private final IMantenimientoRepository mantenimientoRepository;
    private final IVehiculoRepository vehiculoRepository;

    public MantenimientoService(IMantenimientoRepository mantenimientoRepository,
            IVehiculoRepository vehiculoRepository) {
        this.mantenimientoRepository = mantenimientoRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    @Transactional(readOnly = true)
    public List<MantenimientoDto> listar() {
        return mantenimientoRepository.findAll(Sort.by(Sort.Direction.DESC, "idMantenimiento")).stream()
                .map(this::aDto).toList();
    }

    @Transactional(readOnly = true)
    public MantenimientoDto obtener(Long id) {
        return aDto(buscar(id));
    }

    public MantenimientoDto crear(MantenimientoDto dto) {
        Long idVehiculo = Validar.noNulo(dto.getIdVehiculo(), "El vehículo");
        Vehiculo vehiculo = vehiculoRepository.findById(idVehiculo)
                .orElseThrow(() -> new NoSuchElementException("Vehículo no encontrado: " + idVehiculo));

        Mantenimiento mantenimiento = new Mantenimiento();
        aplicarDatos(mantenimiento, dto);

        vehiculo.iniciarMantenimiento();
        mantenimiento.setVehiculo(vehiculo);

        vehiculoRepository.save(vehiculo);
        return aDto(mantenimientoRepository.save(mantenimiento));
    }

    public MantenimientoDto actualizar(Long id, MantenimientoDto dto) {
        Mantenimiento mantenimiento = buscar(id);
        if (dto.getIdVehiculo() != null
                && !dto.getIdVehiculo().equals(mantenimiento.getVehiculo().getIdVehiculo())) {
            throw new IllegalArgumentException(
                    "No se puede cambiar el vehículo de un mantenimiento. Elimínalo y regístralo de nuevo.");
        }
        aplicarDatos(mantenimiento, dto);
        return aDto(mantenimientoRepository.save(mantenimiento));
    }

    public MantenimientoDto finalizar(Long id) {
        Mantenimiento mantenimiento = buscar(id);
        if (!esActivo(mantenimiento)) {
            throw new IllegalStateException("Este mantenimiento ya no está activo.");
        }
        Vehiculo vehiculo = mantenimiento.getVehiculo();
        vehiculo.terminarMantenimiento();
        vehiculoRepository.save(vehiculo);
        return aDto(mantenimiento);
    }

    public void eliminar(Long id) {
        Mantenimiento mantenimiento = buscar(id);
        if (esActivo(mantenimiento)) {
            mantenimiento.getVehiculo().terminarMantenimiento();
        }
        mantenimientoRepository.delete(mantenimiento);
    }

    private Mantenimiento buscar(Long id) {
        return mantenimientoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Mantenimiento no encontrado: " + id));
    }

    private boolean esActivo(Mantenimiento m) {
        Vehiculo v = m.getVehiculo();
        return v.getEstado() == EstadoVehiculo.MANTENIMIENTO
                && !mantenimientoRepository.existsByVehiculo_IdVehiculoAndIdMantenimientoGreaterThan(
                        v.getIdVehiculo(), m.getIdMantenimiento());
    }

    private void aplicarDatos(Mantenimiento m, MantenimientoDto dto) {
        Double costo = Validar.noNulo(dto.getCosto(), "El costo");
        Integer km = Validar.noNulo(dto.getKilometraje(), "El kilometraje");
        if (costo < 0) throw new IllegalArgumentException("El costo no puede ser negativo.");
        if (km < 0) throw new IllegalArgumentException("El kilometraje no puede ser negativo.");

        m.setFechaMantenimiento(Validar.noNulo(dto.getFechaMantenimiento(), "La fecha de mantenimiento"));
        m.setDescripcion(Validar.texto(dto.getDescripcion(), "La descripción"));
        m.setCosto(costo);
        m.setKilometraje(km);
    }

    private MantenimientoDto aDto(Mantenimiento m) {
        Vehiculo v = m.getVehiculo();
        MantenimientoDto dto = new MantenimientoDto();
        dto.setIdMantenimiento(m.getIdMantenimiento());
        dto.setIdVehiculo(v.getIdVehiculo());
        dto.setVehiculoDescripcion(v.getMarca() + " " + v.getModelo() + " (" + v.getMatricula() + ")");
        dto.setFechaMantenimiento(m.getFechaMantenimiento());
        dto.setDescripcion(m.getDescripcion());
        dto.setCosto(m.getCosto());
        dto.setKilometraje(m.getKilometraje());
        dto.setEstadoVehiculo(v.getEstado().name());
        dto.setActivo(esActivo(m));
        return dto;
    }
}