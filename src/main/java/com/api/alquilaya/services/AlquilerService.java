package com.api.alquilaya.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.alquilaya.models.dto.AlquilerDto;
import com.api.alquilaya.models.Alquiler;
import com.api.alquilaya.models.Cliente;
import com.api.alquilaya.models.Vehiculo;
import com.api.alquilaya.models.Vehiculo.EstadoVehiculo;
import com.api.alquilaya.repositories.IAlquilerRepository;
import com.api.alquilaya.repositories.IClienteRepository;
import com.api.alquilaya.repositories.IVehiculoRepository;
import com.api.alquilaya.utils.Validar;

@Service
@Transactional
public class AlquilerService {

    private final IAlquilerRepository alquilerRepository;
    private final IVehiculoRepository vehiculoRepository;
    private final IClienteRepository clienteRepository;

    public AlquilerService(IAlquilerRepository alquilerRepository, IVehiculoRepository vehiculoRepository,
            IClienteRepository clienteRepository) {
        this.alquilerRepository = alquilerRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<AlquilerDto> listar() {
        return alquilerRepository.findAll(Sort.by(Sort.Direction.DESC, "idAlquiler")).stream().map(this::aDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AlquilerDto obtener(Long id) {
        return aDto(buscar(id));
    }

    public AlquilerDto crear(AlquilerDto dto) {
        int dias = calcularDias(dto.getFechaInicio(), dto.getFechaDevolucion());

        Long idVehiculo = Validar.noNulo(dto.getIdVehiculo(), "El vehículo");
        Vehiculo vehiculo = vehiculoRepository.findById(idVehiculo)
                .orElseThrow(() -> new NoSuchElementException("Vehículo no encontrado: " + idVehiculo));
        Long idCliente = Validar.noNulo(dto.getIdCliente(), "El cliente");
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new NoSuchElementException("Cliente no encontrado: " + idCliente));

        vehiculo.alquilar(cliente);

        Alquiler alquiler = new Alquiler();
        alquiler.setVehiculo(vehiculo);
        alquiler.setCliente(cliente);
        alquiler.setFechaInicio(dto.getFechaInicio());
        alquiler.setFechaDevolucion(dto.getFechaDevolucion());
        alquiler.setCostoTotal(redondear(vehiculo.calcularPrecioPorAlquiler(dias)));

        vehiculoRepository.save(vehiculo);
        return aDto(alquilerRepository.save(alquiler));
    }

    public AlquilerDto actualizar(Long id, AlquilerDto dto) {
        Alquiler alquiler = buscar(id);
        int dias = calcularDias(dto.getFechaInicio(), dto.getFechaDevolucion());
        alquiler.setFechaInicio(dto.getFechaInicio());
        alquiler.setFechaDevolucion(dto.getFechaDevolucion());
        alquiler.setCostoTotal(redondear(alquiler.getVehiculo().calcularPrecioPorAlquiler(dias)));
        return aDto(alquilerRepository.save(alquiler));
    }

    public AlquilerDto finalizar(Long id) {
        Alquiler alquiler = buscar(id);
        if (!esActivo(alquiler)) {
            throw new IllegalStateException("Este alquiler ya no está activo.");
        }
        Vehiculo vehiculo = alquiler.getVehiculo();
        vehiculo.devolver();
        vehiculoRepository.save(vehiculo);
        return aDto(alquiler);
    }

    public void eliminar(Long id) {
        Alquiler alquiler = buscar(id);
        if (esActivo(alquiler)) {
            alquiler.getVehiculo().devolver();
        }
        alquilerRepository.delete(alquiler);
    }

    private Alquiler buscar(Long id) {
        return alquilerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Alquiler no encontrado: " + id));
    }

    private int calcularDias(LocalDate inicio, LocalDate fin) {
        Validar.noNulo(inicio, "La fecha de inicio");
        Validar.noNulo(fin, "La fecha de devolución");
        long dias = ChronoUnit.DAYS.between(inicio, fin);
        if (dias <= 0) {
            throw new IllegalArgumentException("La fecha de devolución debe ser posterior a la fecha de inicio.");
        }
        return (int) dias;
    }

    private double redondear(double valor) {
        return Math.round(valor * 100) / 100.0;
    }

    private boolean esActivo(Alquiler a) {
        Vehiculo v = a.getVehiculo();
        return v.getEstado() == EstadoVehiculo.ALQUILADO
                && !alquilerRepository.existsByVehiculo_IdVehiculoAndIdAlquilerGreaterThan(v.getIdVehiculo(),
                        a.getIdAlquiler());
    }

    private AlquilerDto aDto(Alquiler a) {
        Vehiculo v = a.getVehiculo();
        Cliente c = a.getCliente();
        AlquilerDto dto = new AlquilerDto();
        dto.setIdAlquiler(a.getIdAlquiler());
        dto.setIdVehiculo(v.getIdVehiculo());
        dto.setIdCliente(c.getIdCliente());
        dto.setVehiculoDescripcion(v.getMarca() + " " + v.getModelo() + " (" + v.getMatricula() + ")");
        dto.setClienteNombre(c.getNombre() + " " + c.getApellido());
        dto.setFechaInicio(a.getFechaInicio());
        dto.setFechaDevolucion(a.getFechaDevolucion());
        dto.setDias((int) ChronoUnit.DAYS.between(a.getFechaInicio(), a.getFechaDevolucion()));
        dto.setCostoTotal(a.getCostoTotal());
        dto.setEstadoVehiculo(v.getEstado().name());
        dto.setActivo(esActivo(a));
        return dto;
    }
}