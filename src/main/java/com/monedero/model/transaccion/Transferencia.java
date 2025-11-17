package com.monedero.model.transaccion;

import com.monedero.model.monedero.Monedero;

public class Transferencia extends Transaccion {

    public Transferencia(double monto, Monedero origen, Monedero destino) {
        super(monto, origen, destino);
    }

    @Override
    public void ejecutar() throws Exception {
        origen.debitar(monto, this);
        destino.acreditar(monto, this);
    }

    @Override
    public String getDescripcion() {
        return "Transferencia de " + monto + " de " + origen.getId() +
                " a " + destino.getId();
    }
}
