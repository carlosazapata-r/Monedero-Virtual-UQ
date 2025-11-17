package com.monedero.model.transaccion;

import com.monedero.model.Cliente;
import com.monedero.model.monedero.Monedero;
import com.monedero.service.SistemaMonedero;

import java.time.LocalDate;

public class TransaccionProgramada {

    public enum Tipo {
        DEPOSITO,
        TRANSFERENCIA
    }

    private Tipo tipo;
    private Cliente clienteOrigen;
    private Monedero monederoOrigen;
    private Cliente clienteDestino;
    private Monedero monederoDestino;
    private double monto;

    private LocalDate fechaEjecucion;
    private boolean recurrente;
    private int intervaloDias; // cada cuántos días se repite

    public TransaccionProgramada(
            Tipo tipo,
            Cliente clienteOrigen,
            Monedero monederoOrigen,
            Cliente clienteDestino,
            Monedero monederoDestino,
            double monto,
            LocalDate fechaEjecucion,
            boolean recurrente,
            int intervaloDias
    ) {
        this.tipo = tipo;
        this.clienteOrigen = clienteOrigen;
        this.monederoOrigen = monederoOrigen;
        this.clienteDestino = clienteDestino;
        this.monederoDestino = monederoDestino;
        this.monto = monto;
        this.fechaEjecucion = fechaEjecucion;
        this.recurrente = recurrente;
        this.intervaloDias = intervaloDias;
    }

    public LocalDate getFechaEjecucion() {
        return fechaEjecucion;
    }

    public boolean esRecurrente() {
        return recurrente;
    }

    public void ejecutar(SistemaMonedero sistema) throws Exception {
        switch (tipo) {
            case DEPOSITO:
                sistema.realizarDeposito(clienteOrigen, monederoOrigen, monto);
                break;

            case TRANSFERENCIA:
                sistema.realizarTransferencia(
                        clienteOrigen, monederoOrigen,
                        clienteDestino, monederoDestino,
                        monto
                );
                break;
        }

        if (recurrente) {
            fechaEjecucion = fechaEjecucion.plusDays(intervaloDias);
        }
    }
}

