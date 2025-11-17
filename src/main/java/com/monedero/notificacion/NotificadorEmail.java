package com.monedero.notificacion;

import com.monedero.model.Cliente;

public class NotificadorEmail implements Notificador {
    @Override
    public void notificar(Cliente cliente, String mensaje) {
        System.out.println("[EMAIL] A " + cliente.getNombre() + ": " + mensaje);
    }
}

