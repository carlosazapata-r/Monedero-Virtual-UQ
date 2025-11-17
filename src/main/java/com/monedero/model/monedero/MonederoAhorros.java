package com.monedero.model.monedero;

public class MonederoAhorros extends Monedero {

    public MonederoAhorros(String id) {
        super(id);
    }

    @Override
    public String getTipo() {
        return "Ahorros";
    }
}
