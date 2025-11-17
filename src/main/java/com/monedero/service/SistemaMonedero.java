package com.monedero.service;

import com.monedero.model.Cliente;
import com.monedero.model.monedero.Monedero;
import com.monedero.model.transaccion.Deposito;
import com.monedero.model.transaccion.Retiro;
import com.monedero.model.transaccion.Transferencia;
import com.monedero.model.transaccion.TransaccionProgramada;
import com.monedero.notificacion.Notificador;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SistemaMonedero {

    private List<Cliente> clientes;
    private Notificador notificador;


    private List<TransaccionProgramada> transaccionesProgramadas;

    public SistemaMonedero(Notificador notificador) {
        this.clientes = new ArrayList<>();
        this.notificador = notificador;
        this.transaccionesProgramadas = new ArrayList<>();
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

    public Cliente buscarClientePorNombre(String nombre) {
        return clientes.stream()
                .filter(c -> c.getNombre().equals(nombre))
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
        // Cargo base del 1%
        double cargo = monto * 0.01;

        // Si tiene beneficio de retiros sin cargo, no se cobra
        if (cliente.tieneRetirosSinCargoVigente()) {
            cargo = 0;
        }

        Retiro r = new Retiro(monto, monedero);
        r.ejecutar();

        // Si hay cargo, se debita sin registrar transacción (null)
        if (cargo > 0) {
            monedero.debitar(cargo, null);
        }

        GestorPuntos.aplicarPuntos(cliente, r);
        verificarSaldoBajo(cliente, monedero);
    }


    public void realizarTransferencia(Cliente cliente,
                                      Monedero origen,
                                      Monedero destino,
                                      double monto) throws Exception {
        Transferencia t = new Transferencia(monto, origen, destino);
        t.ejecutar();
        GestorPuntos.aplicarPuntos(cliente, t);
        verificarSaldoBajo(cliente, origen);
    }

    public void realizarTransferencia(Cliente clienteOrigen,
                                      Monedero monederoOrigen,
                                      Cliente clienteDestino,
                                      Monedero monederoDestino,
                                      double monto) throws Exception {

        // Comisión base 2%
        double comision = monto * 0.02;

        // Si tiene beneficio de descuento, se reduce 10% y se consume
        if (clienteOrigen.tieneDescuentoTransferencias10()) {
            comision = comision * 0.9;
            clienteOrigen.consumirDescuentoTransferencias10();
        }

        Transferencia t = new Transferencia(monto, monederoOrigen, monederoDestino);
        t.ejecutar();

        // Cobrar comisión como débito sin registrar transacción
        if (comision > 0) {
            monederoOrigen.debitar(comision, null);
        }

        GestorPuntos.aplicarPuntos(clienteOrigen, t);
        verificarSaldoBajo(clienteOrigen, monederoOrigen);
    }

    private void verificarSaldoBajo(Cliente cliente, Monedero monedero) {
        if (monedero.getSaldo() < 50) {
            notificador.notificar(cliente, "Alerta: saldo bajo en monedero " + monedero.getId());
        }
    }


    // Registrar una nueva transacción programada
    public void programarTransaccion(TransaccionProgramada t) {
        transaccionesProgramadas.add(t);
    }

    // Procesar todas las que ya deberían ejecutarse (hoy o antes)
    public void procesarTransaccionesProgramadas() {
        LocalDate hoy = LocalDate.now();

        // Ordenar por fecha de ejecución (algoritmo de ordenamiento que pide el profe)
        Collections.sort(transaccionesProgramadas,
                Comparator.comparing(TransaccionProgramada::getFechaEjecucion));

        List<TransaccionProgramada> ejecutadas = new ArrayList<>();

        for (TransaccionProgramada t : transaccionesProgramadas) {
            if (!t.getFechaEjecucion().isAfter(hoy)) {
                try {
                    t.ejecutar(this);
                } catch (Exception e) {
                    System.out.println("Error al ejecutar transacción programada: " + e.getMessage());
                }

                // Si NO es recurrente, la sacamos de la lista
                if (!t.esRecurrente()) {
                    ejecutadas.add(t);
                }
            }
        }

        transaccionesProgramadas.removeAll(ejecutadas);
    }
}
