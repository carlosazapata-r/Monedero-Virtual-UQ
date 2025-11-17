package com.monedero.model.transaccion;

import com.monedero.model.monedero.Monedero;

import java.time.LocalDateTime;

public abstract class Transaccion implements Ejecutable {

    protected double monto;
    protected LocalDateTime fecha;
    protected Monedero origen;
    protected Monedero destino;

    public Transaccion(double monto, Monedero origen, Monedero destino) {
        this.monto = monto;
        this.origen = origen;
        this.destino = destino;
        this.fecha = LocalDateTime.now();
    }

    public double getMonto() {
        return monto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public abstract String getDescripcion();
}
