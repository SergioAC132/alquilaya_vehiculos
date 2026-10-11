package com.api.alquilaya.models;

import com.api.alquilaya.models.interfaces.Electrico;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
@Entity
@DiscriminatorValue("COCHE_ELECTRICO")
public class CocheElectrico extends Coche implements Electrico {

    private static final double DESCUENTO_ECOLOGICO = 0.15;

    private int nivelBateria;
    private int autonomiaMaximaKm;

    @Override
    public double calcularPrecioPorAlquiler(int dias) {
        double precioCoche = super.calcularPrecioPorAlquiler(dias);
        return precioCoche * (1 - DESCUENTO_ECOLOGICO);
    }

    @Override
    public void cargarBateria() {
        this.nivelBateria = 100;
    }
}