package com.api.alquilaya.models.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AlquilerDto {

    private Long idAlquiler;
    private Long idVehiculo;
    private Long idCliente;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaDevolucion;

    private Double costoTotal;
    private Integer dias;
    private String vehiculoDescripcion;
    private String clienteNombre;
    private String estadoVehiculo;
    private boolean activo;
}
