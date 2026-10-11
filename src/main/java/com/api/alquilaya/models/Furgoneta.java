package com.api.alquilaya.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("FURGONETA")
public class Furgoneta extends Vehiculo {

    private int capacidadCargaKg;

    @Override
    public double calcularPrecioPorAlquiler(int dias) {
        validarDias(dias);
        double precio = getTarifaDiaria() * dias;
        if (capacidadCargaKg > 1000) precio += 15 * dias;
        return precio;
    }
}
