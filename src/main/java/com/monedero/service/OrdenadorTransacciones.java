package com.monedero.service;

import com.monedero.model.monedero.Monedero;
import com.monedero.model.transaccion.Transaccion;

import java.util.ArrayList;
import java.util.List;

public class OrdenadorTransacciones {

    /**
     * Ordenar historial de un monedero por fecha (más antigua -> más reciente)
     * @param monedero
     * @return
     */
    public static List<Transaccion> ordenarPorFecha(Monedero monedero) {
        List<Transaccion> copia = new ArrayList<>(monedero.getHistorial());
        quicksort(copia, 0, copia.size() - 1);
        return copia;
    }


    private static void quicksort(List<Transaccion> lista, int inicio, int fin) {
        if (inicio >= fin) {
            return; // caso base
        }

        int indicePivot = particion(lista, inicio, fin);

        // Llamadas recursivas
        quicksort(lista, inicio, indicePivot - 1);
        quicksort(lista, indicePivot + 1, fin);
    }

    private static int particion(List<Transaccion> lista, int inicio, int fin) {
        Transaccion pivot = lista.get(fin);
        int i = inicio - 1;

        for (int j = inicio; j < fin; j++) {
            // Comparamos fechas: si la transacción j es ANTERIOR o igual al pivot
            if (lista.get(j).getFecha().isBefore(pivot.getFecha())
                    || lista.get(j).getFecha().isEqual(pivot.getFecha())) {
                i++;
                intercambiar(lista, i, j);
            }
        }

        intercambiar(lista, i + 1, fin);
        return i + 1;
    }

    private static void intercambiar(List<Transaccion> lista, int i, int j) {
        Transaccion tmp = lista.get(i);
        lista.set(i, lista.get(j));
        lista.set(j, tmp);
    }
}

