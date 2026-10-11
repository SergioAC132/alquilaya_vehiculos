package com.api.alquilaya.models;


import com.api.alquilaya.models.interfaces.Alquilable;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
@Entity 
@Inheritance(strategy = jakarta.persistence.InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_vehiculo", discriminatorType = jakarta.persistence.DiscriminatorType.STRING)
@Table(name = "vehiculos")
public abstract class Vehiculo implements Alquilable {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo")
    private Long idVehiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_oficina", nullable = false)
    private Oficina oficina;

    @Column(name = "marca", nullable = false)
    private String marca;

    @Column(name = "modelo", nullable = false)
    private String modelo;

    @Column(name = "anio", nullable = false)
    private int anio;

    @Column(name = "matricula", nullable = false)
    private String matricula;

    @Column(name = "tarifa_diaria", nullable = false)
    private double tarifaDiaria;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoVehiculo estado;

    @Column(name = "detalle")
    private String detalle;

    public abstract double calcularPrecioPorAlquiler(int dias);

    protected void validarDias(int dias) {
        if (dias <= 0) {
            throw new IllegalArgumentException("El número de días de alquiler debe ser mayor que cero.");
        }
    }

    @Override
    public void alquilar(Cliente cliente) {
        if (!estaDisponible()) {
            throw new IllegalStateException("El vehículo no está disponible para alquilar.");
        }
        this.estado = EstadoVehiculo.ALQUILADO;
    }

    @Override
    public void devolver() {
        if (this.estado != EstadoVehiculo.ALQUILADO) {
            throw new IllegalStateException("El vehículo no está alquilado.");
        }
        this.estado = EstadoVehiculo.DISPONIBLE;
    }

    // Registrar un mantenimiento: solo si el vehículo está disponible
    public void iniciarMantenimiento() {
        if (this.estado == EstadoVehiculo.ALQUILADO) {
            throw new IllegalStateException("No se puede registrar un mantenimiento: el vehículo está alquilado.");
        }
        if (this.estado == EstadoVehiculo.MANTENIMIENTO) {
            throw new IllegalStateException("El vehículo ya está en mantenimiento.");
        }
        this.estado = EstadoVehiculo.MANTENIMIENTO;
    }

    public void terminarMantenimiento() {
        if (this.estado != EstadoVehiculo.MANTENIMIENTO) {
            throw new IllegalStateException("El vehículo no está en mantenimiento.");
        }
        this.estado = EstadoVehiculo.DISPONIBLE;
    }

    @Override
    public boolean estaDisponible() {
        return this.estado == EstadoVehiculo.DISPONIBLE;
    }

    public enum EstadoVehiculo {
        DISPONIBLE,
        ALQUILADO,
        MANTENIMIENTO
    }
}