package com.monedero.notificacion;

import com.monedero.model.Cliente;

public interface Notificador {
    void notificar(Cliente cliente, String mensaje);
}

