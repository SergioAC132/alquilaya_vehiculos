package com.api.alquilaya.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity 
@DiscriminatorValue("COCHE")
public class Coche extends Vehiculo {

    @Override 
    public double calcularPrecioPorAlquiler(int dias) {
        validarDias(dias);
        double precio = getTarifaDiaria() * dias;
        if (dias > 7) precio *= 0.9;
        return precio; 
    }
}
