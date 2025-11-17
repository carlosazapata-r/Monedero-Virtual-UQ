package com.monedero.service;

import com.monedero.model.Cliente;
import com.monedero.model.RangoCliente;
import com.monedero.model.monedero.MonederoAhorros;
import com.monedero.model.transaccion.Deposito;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GestorPuntosTest {

    @Test
    void aplicarPuntos_asignaPuntosCorrectamente() throws Exception {
        // Cliente con constructor de 3 argumentos (id, nombre, contraseña)
        Cliente c = new Cliente("1", "Carlos", "1234");

        MonederoAhorros m = new MonederoAhorros("AHO-TEST");

        Deposito d = new Deposito(250, m);
        d.ejecutar();

        GestorPuntos.aplicarPuntos(c, d);

        assertEquals(25, c.getPuntos());
    }

    @Test
    void calcularRango_retornaRangoCorrectoSegunPuntos() {
        Cliente c = new Cliente("2", "Samuel", "abcd");

        assertEquals(RangoCliente.BRONCE, GestorPuntos.calcularRango(c));

        c.agregarPuntos(200);
        assertEquals(RangoCliente.PLATA, GestorPuntos.calcularRango(c));

        c.agregarPuntos(300); // total = 500
        assertEquals(RangoCliente.ORO, GestorPuntos.calcularRango(c));

        c.agregarPuntos(500); // total = 1000
        assertEquals(RangoCliente.PLATINO, GestorPuntos.calcularRango(c));
    }
}
