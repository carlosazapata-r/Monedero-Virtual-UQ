package com.monedero.service;

import com.monedero.model.Cliente;
import com.monedero.notificacion.Notificador;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SistemaMonederoTest {

    private Notificador crearNotificadorMock() {
        return (cliente, mensaje) -> {
            // No necesitamos hacer nada en los tests
        };
    }

    @Test
    void registrarCliente_agregaClienteALaLista() {
        Notificador notificador = crearNotificadorMock();
        SistemaMonedero sistema = new SistemaMonedero(notificador);

        Cliente c = new Cliente("1", "Carlos", "1234");

        sistema.registrarCliente(c);

        List<Cliente> clientes = sistema.getClientes();

        assertEquals(1, clientes.size());
        assertSame(c, clientes.get(0));
    }

    @Test
    void buscarClientePorNombre_encuentraClienteCorrecto() {
        Notificador notificador = crearNotificadorMock();
        SistemaMonedero sistema = new SistemaMonedero(notificador);

        Cliente c1 = new Cliente("1", "Carlos", "1234");
        Cliente c2 = new Cliente("2", "Samuel", "abcd");

        sistema.registrarCliente(c1);
        sistema.registrarCliente(c2);

        Cliente resultado = sistema.buscarClientePorNombre("Samuel");

        assertNotNull(resultado);
        assertEquals("2", resultado.getId());
        assertEquals("Samuel", resultado.getNombre());
    }
}
