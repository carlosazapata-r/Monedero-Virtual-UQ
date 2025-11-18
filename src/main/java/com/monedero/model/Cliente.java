package com.monedero.model;

import com.monedero.model.monedero.Monedero;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cliente {

    private String id;
    private String nombre;     // usuario para login
    private String password;   // contraseña

    private List<Monedero> monederos;
    private int puntos;

    private boolean descuentoTransferencias10Activo;

    private LocalDate retirosSinCargoHasta;

    public Cliente(String id, String nombre, String password) {
        this.id = id;
        this.nombre = nombre;
        this.password = password;
        this.monederos = new ArrayList<>();
        this.puntos = 0;
        this.descuentoTransferencias10Activo = false;
        this.retirosSinCargoHasta = null;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPassword() {
        return password;
    }

    public List<Monedero> getMonederos() {
        return Collections.unmodifiableList(monederos);
    }

    public void agregarMonedero(Monedero m) {
        this.monederos.add(m);
    }


    public int getPuntos() {
        return puntos;
    }

    /**
     * añadir puntos a la cuenta
     * @param cantidad
     */
    public void agregarPuntos(int cantidad) {
        this.puntos += cantidad;
        if (this.puntos < 0) {
            this.puntos = 0;
        }
    }

    /**
     * funcionamiento de descuentoTransferencia
     */
    public void activarDescuentoTransferencias10() {
        this.descuentoTransferencias10Activo = true;
    }

    public boolean tieneDescuentoTransferencias10() {
        return descuentoTransferencias10Activo;
    }

    public void consumirDescuentoTransferencias10() {
        this.descuentoTransferencias10Activo = false;
    }

    /**
     * metodo para funcionamiento de cargo
     */

    public void activarRetirosSinCargoUnMes() {
        this.retirosSinCargoHasta = LocalDate.now().plusMonths(1);
    }

    public boolean tieneRetirosSinCargoVigente() {
        return retirosSinCargoHasta != null
                && !LocalDate.now().isAfter(retirosSinCargoHasta);
    }

    @Override
    public String toString() {
        return nombre;
    }
}