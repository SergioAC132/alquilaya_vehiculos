package com.api.alquilaya.models.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VehiculoDto {

    private Long idVehiculo;
    // COCHE, COCHE_ELECTRICO, FURGONETA o MOTO
    private String tipo;
    private String tipoEtiqueta;
    private Long idOficina;
    private String oficinaNombre;
    private String marca;
    private String modelo;
    private Integer anio;
    private String matricula;
    private Double tarifaDiaria;
    // DISPONIBLE, ALQUILADO o MANTENIMIENTO
    private String estado;

    // campos propios de cada tipo
    private Integer cilindrada;
    private Integer capacidadCargaKg;
    private Integer nivelBateria;
    private Integer autonomiaMaximaKm;

    // texto libre opcional
    private String detalle;

    // resumen de los campos propios del tipo (solo respuesta)
    private String resumen;
}