package com.monedero.model.transaccion;

import com.monedero.model.monedero.Monedero;

import java.time.LocalDateTime;

public class TransaccionProgramada extends Transaccion {

    private LocalDateTime fechaEjecucion;

    public TransaccionProgramada(double monto,
                                 Monedero origen,
                                 Monedero destino,
                                 LocalDateTime fechaEjecucion) {
        super(monto, origen, destino);
        this.fechaEjecucion = fechaEjecucion;
    }

    public LocalDateTime getFechaEjecucion() {
        return fechaEjecucion;
    }

    @Override
    public void ejecutar() throws Exception {
        // Validar fecha
        if (LocalDateTime.now().isBefore(fechaEjecucion)) {
            throw new Exception("Aún no es la fecha de ejecución.");
        }

        // Usamos los métodos de Monedero:
        origen.debitar(monto, this);   // resta y registra en origen
        destino.acreditar(monto, this); // suma y registra en destino
    }

    @Override
    public String getDescripcion() {
        return "Transacción programada de " + monto +
                " de " + origen.getId() +
                " a " + destino.getId() +
                " para " + fechaEjecucion;
    }
}
