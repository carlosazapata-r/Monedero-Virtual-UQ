package com.monedero.service;

import com.monedero.model.Cliente;
import com.monedero.model.monedero.Monedero;
import com.monedero.model.transaccion.Deposito;
import com.monedero.model.transaccion.Retiro;
import com.monedero.model.transaccion.Transferencia;
import com.monedero.notificacion.Notificador;

import java.util.ArrayList;
import java.util.List;

public class SistemaMonedero {

    private List<Cliente> clientes;
    private Notificador notificador;

    public SistemaMonedero(Notificador notificador) {
        this.clientes = new ArrayList<>();
        this.notificador = notificador;
    }

    public void registrarCliente(Cliente c) {
        clientes.add(c);
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public Cliente buscarClientePorId(String id) {
        return clientes.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void realizarDeposito(Cliente cliente, Monedero monedero, double monto) throws Exception {
        Deposito d = new Deposito(monto, monedero);
        d.ejecutar();
        GestorPuntos.aplicarPuntos(cliente, d);
        verificarSaldoBajo(cliente, monedero);
    }

    public void realizarRetiro(Cliente cliente, Monedero monedero, double monto) throws Exception {
        Retiro r = new Retiro(monto, monedero);
        r.ejecutar();
        GestorPuntos.aplicarPuntos(cliente, r);
        verificarSaldoBajo(cliente, monedero);
    }

    public void realizarTransferencia(Cliente cliente, Monedero origen, Monedero destino, double monto) throws Exception {
        Transferencia t = new Transferencia(monto, origen, destino);
        t.ejecutar();
        GestorPuntos.aplicarPuntos(cliente, t);
        verificarSaldoBajo(cliente, origen);
    }

    private void verificarSaldoBajo(Cliente cliente, Monedero monedero) {
        if (monedero.getSaldo() < 50) {
            notificador.notificar(cliente, "Alerta: saldo bajo en monedero " + monedero.getId());
        }
    }

    public Cliente buscarClientePorNombre(String nombre) {
        return clientes.stream()
                .filter(c -> c.getNombre().equals(nombre))
                .findFirst()
                .orElse(null);
    }

}
