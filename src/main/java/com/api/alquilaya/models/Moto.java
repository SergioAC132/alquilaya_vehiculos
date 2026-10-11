package com.api.alquilaya.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("MOTO")
public class Moto extends Vehiculo {

    private int cilindrada;

    @Override
    public double calcularPrecioPorAlquiler(int dias) {
        validarDias(dias);
        double base = (getTarifaDiaria() + 3) * dias;
        if (cilindrada > 500) base += 5 * dias;
        return base;
    }
}
