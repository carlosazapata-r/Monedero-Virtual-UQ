package com.monedero.service;

import com.monedero.model.monedero.Monedero;
import com.monedero.model.transaccion.Retiro;
import com.monedero.model.transaccion.Transaccion;
import com.monedero.model.transaccion.Transferencia;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class AnalizadorGastos {
    /**
     * metodo para calcular el total gastado de un cliente
     * @param monedero
     * @return
     */
    public static double totalGastado(Monedero monedero) {
        return monedero.getHistorial().stream()
                .filter(t -> t instanceof Retiro || t instanceof Transferencia)
                .mapToDouble(Transaccion::getMonto)
                .sum();
    }

    /**
     * metodo para calcular el gasto de un cliente en promedio
     * @param monedero
     * @return
     */
    public static double gastoPromedioPorDia(Monedero monedero) {
        Map<LocalDate, Double> gastoPorDia = new HashMap<>();

        for (Transaccion t : monedero.getHistorial()) {
            if (t instanceof Retiro || t instanceof Transferencia) {
                LocalDate dia = t.getFecha().toLocalDate();
                gastoPorDia.put(dia, gastoPorDia.getOrDefault(dia, 0.0) + t.getMonto());
            }
        }

        if (gastoPorDia.isEmpty()) return 0;
        double total = gastoPorDia.values().stream().mapToDouble(Double::doubleValue).sum();
        return total / gastoPorDia.size();
    }
}
