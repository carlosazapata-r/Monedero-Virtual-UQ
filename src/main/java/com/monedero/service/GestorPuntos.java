package com.monedero.service;

import com.monedero.model.Beneficio;
import com.monedero.model.Cliente;
import com.monedero.model.RangoCliente;
import com.monedero.model.transaccion.Deposito;
import com.monedero.model.transaccion.Retiro;
import com.monedero.model.transaccion.Transaccion;
import com.monedero.model.transaccion.Transferencia;

import java.time.LocalDate;

public class GestorPuntos {

    public static void aplicarPuntos(Cliente cliente, Transaccion t) {
        double monto = t.getMonto();
        int puntos = (int) (monto / 10);   // 1 punto cada 10 unidades
        cliente.agregarPuntos(puntos);
    }

    public static RangoCliente calcularRango(Cliente c) {
        int p = c.getPuntos();
        if (p >= 1000) return RangoCliente.PLATINO;
        if (p >= 500) return RangoCliente.ORO;
        if (p >= 200) return RangoCliente.PLATA;
        return RangoCliente.BRONCE;
    }

    /**
     * Canjea puntos por beneficios:
     * 100 puntos  -> descuento 10% en comisión de transferencias (una vez)
     * 500 puntos  -> retiros sin cargo durante 1 mes
     * 1000 puntos -> bono de 50 unidades (se aplicará al monedero seleccionado desde la interfaz)
     */
    public static void canjear(Cliente cliente, int puntosACanjear) throws Exception {
        if (cliente.getPuntos() < puntosACanjear) {
            throw new Exception("No tienes puntos suficientes para este canje.");
        }

        switch (puntosACanjear) {
            case 100:
                cliente.agregarPuntos(-100);
                cliente.activarDescuentoTransferencias10();
                break;

            case 500:
                cliente.agregarPuntos(-500);
                cliente.activarRetirosSinCargoUnMes();
                break;

            case 1000:
                // Solo descontamos puntos aquí.
                // El bono de 50 se aplicará desde el controlador
                // sobre el monedero que esté seleccionado.
                cliente.agregarPuntos(-1000);
                break;

            default:
                throw new Exception("No existe un beneficio para esa cantidad de puntos.");
        }
    }
}