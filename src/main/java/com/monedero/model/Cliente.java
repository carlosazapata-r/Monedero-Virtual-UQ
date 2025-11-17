package com.monedero.model;

import com.monedero.model.monedero.Monedero;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cliente {

    private String id;
    private String nombre;
    private String password;   // contraseña
    private List<Monedero> monederos;
    private int puntos;

    public Cliente(String id, String nombre, String password) {
        this.id = id;
        this.nombre = nombre;
        this.password = password;
        this.monederos = new ArrayList<>();
        this.puntos = 0;
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

    public void agregarPuntos(int puntos) {
        this.puntos += puntos;
    }

    @Override
    public String toString() {
        return nombre + " (ID: " + id + ")";
    }
}

