package com.monedero.service;

import com.monedero.model.monedero.MonederoAhorros;
import com.monedero.model.transaccion.Deposito;
import com.monedero.model.transaccion.Retiro;
import com.monedero.model.transaccion.Transaccion;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrdenadorTransaccionesTest {

    @Test
    void ordenarPorFecha_devuelveHistorialOrdenadoAscendente() throws Exception {
        MonederoAhorros monedero = new MonederoAhorros("AHO-TEST");

        // Creamos varias transacciones y las ejecutamos en ORDEN cronológico
        Deposito d1 = new Deposito(100, monedero);
        d1.ejecutar();

        Retiro r1 = new Retiro(20, monedero);
        r1.ejecutar();

        Deposito d2 = new Deposito(50, monedero);
        d2.ejecutar();

        List<Transaccion> ordenadas = OrdenadorTransacciones.ordenarPorFecha(monedero);

        for (int i = 0; i < ordenadas.size() - 1; i++) {
            Transaccion actual = ordenadas.get(i);
            Transaccion siguiente = ordenadas.get(i + 1);

            assertTrue(
                    !actual.getFecha().isAfter(siguiente.getFecha()),
                    "La lista no está ordenada por fecha ascendente"
            );
        }

        assertEquals(monedero.getHistorial().size(), ordenadas.size());
    }

    @Test
    void quicksort_ordenaListaDesordenadaPorFechaAscendente() throws Exception {
        MonederoAhorros monedero = new MonederoAhorros("AHO-QS");

        Deposito d1 = new Deposito(100, monedero);
        d1.ejecutar();

        Deposito d2 = new Deposito(200, monedero);
        d2.ejecutar();

        Deposito d3 = new Deposito(300, monedero);
        d3.ejecutar();

        List<Transaccion> lista = new ArrayList<>(Arrays.asList(d3, d1, d2));

        Method quicksort = OrdenadorTransacciones.class.getDeclaredMethod(
                "quicksort", List.class, int.class, int.class
        );
        quicksort.setAccessible(true);
        quicksort.invoke(null, lista, 0, lista.size() - 1);

        for (int i = 0; i < lista.size() - 1; i++) {
            Transaccion actual = lista.get(i);
            Transaccion siguiente = lista.get(i + 1);

            assertTrue(
                    !actual.getFecha().isAfter(siguiente.getFecha()),
                    "La lista no está ordenada por fecha ascendente tras aplicar quicksort"
            );
        }
    }
}
