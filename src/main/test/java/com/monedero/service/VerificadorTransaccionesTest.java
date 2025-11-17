package com.monedero.service;

import com.monedero.model.monedero.MonederoAhorros;
import com.monedero.model.transaccion.Transaccion;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class VerificadorTransaccionesTest {

    private Transaccion crearTransaccionValida(double monto) {
        MonederoAhorros m1 = new MonederoAhorros("M1");
        MonederoAhorros m2 = new MonederoAhorros("M2");

        return new Transaccion(monto, m1, m2) {
            @Override
            public void ejecutar() { }

            @Override
            public String getDescripcion() {
                return "Mock válida";
            }
        };
    }

    private Transaccion crearTransaccionInvalida(double monto) {
        MonederoAhorros m1 = new MonederoAhorros("M1");
        MonederoAhorros m2 = new MonederoAhorros("M2");

        return new Transaccion(monto, m1, m2) {
            @Override
            public void ejecutar() { }

            @Override
            public String getDescripcion() {
                return "Mock inválida";
            }
        };
    }

    @Test
    void verificarLista_devuelveTrue_siTodosLosMontosSonPositivos() {
        List<Transaccion> lista = new ArrayList<>();
        lista.add(crearTransaccionValida(50));
        lista.add(crearTransaccionValida(20));
        lista.add(crearTransaccionValida(10));

        boolean resultado = VerificadorTransacciones.verificarLista(lista);

        assertTrue(resultado);
    }

    @Test
    void verificarLista_devuelveFalse_siAlgunaTransaccionTieneMontoCeroONegativo() {
        List<Transaccion> lista = new ArrayList<>();
        lista.add(crearTransaccionValida(30));
        lista.add(crearTransaccionInvalida(0));   // aquí está el monto inválido
        lista.add(crearTransaccionValida(10));

        boolean resultado = VerificadorTransacciones.verificarLista(lista);

        assertFalse(resultado);
    }
}
