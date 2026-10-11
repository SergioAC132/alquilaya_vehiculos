package com.api.alquilaya.models.interfaces;

import com.api.alquilaya.models.Cliente;

public interface Alquilable {
    void alquilar(Cliente cliente);
    void devolver();
    boolean estaDisponible();
}
