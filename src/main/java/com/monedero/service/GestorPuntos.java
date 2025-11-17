package com.monedero.service;

import com.monedero.model.Beneficio;
import com.monedero.model.Cliente;
import com.monedero.model.RangoCliente;
import com.monedero.model.transaccion.Deposito;
import com.monedero.model.transaccion.Retiro;
import com.monedero.model.transaccion.Transaccion;
import com.monedero.model.transaccion.Transferencia;

public class GestorPuntos {

    public static int calcularPuntos(Transaccion t) {
        double monto = t.getMonto();
        int factor = 0;

        if (t instanceof Deposito) {
            factor = 1;
        } else if (t instanceof Retiro) {
            factor = 2;
        } else if (t instanceof Transferencia) {
            factor = 3;
        }

        return (int) ((monto / 100.0) * factor);
    }

    public static void aplicarPuntos(Cliente cliente, Transaccion t) {
        int puntos = calcularPuntos(t);
        cliente.agregarPuntos(puntos);
    }

    public static RangoCliente calcularRango(Cliente cliente) {
        int p = cliente.getPuntos();
        if (p <= 500) return RangoCliente.BRONCE;
        if (p <= 1000) return RangoCliente.PLATA;
        if (p <= 5000) return RangoCliente.ORO;
        return RangoCliente.PLATINO;
    }

    public static Beneficio canjear(Cliente cliente, int puntosACanjear) throws Exception {
        if (cliente.getPuntos() < puntosACanjear) {
            throw new Exception("Puntos insuficientes");
        }

        if (puntosACanjear == 100) {
            cliente.agregarPuntos(-100);
            return Beneficio.DESCUENTO_TRANSFERENCIAS_10;
        } else if (puntosACanjear == 500) {
            cliente.agregarPuntos(-500);
            return Beneficio.SIN_CARGOS_RETIROS_1_MES;
        } else if (puntosACanjear == 1000) {
            cliente.agregarPuntos(-1000);
            return Beneficio.BONO_SALDO_50;
        } else {
            throw new Exception("Cantidad de puntos no válida para canje");
        }
    }
}
