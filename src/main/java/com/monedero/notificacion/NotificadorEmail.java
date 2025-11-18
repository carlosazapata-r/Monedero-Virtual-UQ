package com.monedero.notificacion;

import com.monedero.model.Cliente;

public class NotificadorEmail implements Notificador {
    /**
     * metodo para notifcar al correo
     * @param cliente
     * @param mensaje
     */
    @Override
    public void notificar(Cliente cliente, String mensaje) {
        System.out.println("[EMAIL] A " + cliente.getNombre() + ": " + mensaje);
    }
}

