package com.monedero.model.monedero;

import com.monedero.model.transaccion.Transaccion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Monedero {

    private String id;
    private double saldo;
    private List<Transaccion> historial;

    public Monedero(String id) {
        this.id = id;
        this.saldo = 0;
        this.historial = new ArrayList<>();
    }

    public abstract String getTipo();

    public String getId() {
        return id;
    }

    public double getSaldo() {
        return saldo;
    }

    public List<Transaccion> getHistorial() {
        return Collections.unmodifiableList(historial);
    }

    /**
     * metodo para ingresar dinero en un monedero
     * @param monto
     * @param t
     */
    public void acreditar(double monto, Transaccion t) {
        if (monto <= 0) return;

        this.saldo += monto;

        if (t != null) {
            historial.add(t);
        }
    }

    /**
     * metodo para descontar dinero de un monedero
     * @param monto
     * @param t
     * @throws Exception
     */
    public void debitar(double monto, Transaccion t) throws Exception {
        if (monto <= 0) {
            throw new Exception("El monto debe ser mayor que cero.");
        }
        if (saldo < monto) {
            throw new Exception("Saldo insuficiente.");
        }

        this.saldo -= monto;

        if (t != null) {
            historial.add(t);
        }
    }

    /**
     * metodo para registrar una transaccion
     * @param t
     */
    private void registrarTransaccion(Transaccion t) {
        historial.add(t);
    }

    @Override
    public String toString() {
        return getTipo() + " (" + id + ") - Saldo: " + saldo;
    }
}
