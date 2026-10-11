package com.api.alquilaya.utils;

public final class Validar {

    private Validar() {
    }

    public static String texto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " es obligatorio.");
        }
        return valor.trim();
    }

    public static <T> T noNulo(T valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException(campo + " es obligatorio.");
        }
        return valor;
    }
}
