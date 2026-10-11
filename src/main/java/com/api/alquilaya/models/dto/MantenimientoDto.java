package com.api.alquilaya.models.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MantenimientoDto {

    private Long idMantenimiento;
    private Long idVehiculo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaMantenimiento;

    private String descripcion;
    private Double costo;
    private Integer kilometraje;

    private String vehiculoDescripcion;
    private String estadoVehiculo;
    private boolean activo;
}