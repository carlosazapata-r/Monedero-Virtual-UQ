package com.monedero.service;

import com.monedero.model.transaccion.Transaccion;

import java.util.List;

public class VerificadorTransacciones {

    public static boolean verificarLista(List<Transaccion> lista) {
        return verificarRecursivo(lista, 0);
    }

    private static boolean verificarRecursivo(List<Transaccion> lista, int index) {
        if (index >= lista.size()) {
            return true;
        }
        Transaccion t = lista.get(index);
        if (t.getMonto() <= 0) {
            return false;
        }
        return verificarRecursivo(lista, index + 1);
    }
}
