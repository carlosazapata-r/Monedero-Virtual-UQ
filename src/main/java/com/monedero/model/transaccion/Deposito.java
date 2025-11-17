package com.monedero.model.transaccion;

import com.monedero.model.monedero.Monedero;

public class Deposito extends Transaccion {

    public Deposito(double monto, Monedero destino) {
        super(monto, null, destino);
    }

    @Override
    public void ejecutar() throws Exception {
        destino.acreditar(monto, this);
    }

    @Override
    public String getDescripcion() {
        return "Depósito de " + monto + " a " + destino.getId();
    }
}

