package com.monedero.model.monedero;

public class MonederoGastos extends Monedero {

    public MonederoGastos(String id) {
        super(id);
    }

    @Override
    public String getTipo() {
        return "Gastos diarios";
    }
}
