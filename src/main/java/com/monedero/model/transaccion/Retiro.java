package com.monedero.model.transaccion;

import com.monedero.model.monedero.Monedero;

public class Retiro extends Transaccion {

    public Retiro(double monto, Monedero origen) {
        super(monto, origen, null);
    }

    @Override
    public void ejecutar() throws Exception {
        origen.debitar(monto, this);
    }

    @Override
    public String getDescripcion() {
        return "Retiro de " + monto + " de " + origen.getId();
    }
}
