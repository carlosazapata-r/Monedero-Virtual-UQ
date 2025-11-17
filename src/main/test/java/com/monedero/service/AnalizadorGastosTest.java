package com.monedero.service;

import com.monedero.model.monedero.MonederoAhorros;
import com.monedero.model.monedero.MonederoGastos;
import com.monedero.model.transaccion.Deposito;
import com.monedero.model.transaccion.Retiro;
import com.monedero.model.transaccion.Transferencia;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnalizadorGastosTest {

    @Test
    void totalGastado_sumaSoloRetirosYTransferencias() throws Exception {
        MonederoAhorros mAho = new MonederoAhorros("AHO-TEST");
        MonederoGastos mGas = new MonederoGastos("GAS-TEST");

        new Deposito(500, mAho).ejecutar();

        new Retiro(100, mAho).ejecutar();
        new Transferencia(50, mAho, mGas).ejecutar();

        new Deposito(200, mAho).ejecutar();

        double total = AnalizadorGastos.totalGastado(mAho);
        assertEquals(150.0, total, 0.001);
    }

    @Test
    void gastoPromedioPorDia_conTransaccionesEnUnSoloDia() throws Exception {
        MonederoAhorros mAho = new MonederoAhorros("AHO-TEST2");

        new Deposito(300, mAho).ejecutar();

        new Retiro(40, mAho).ejecutar();
        new Retiro(60, mAho).ejecutar();

        double promedio = AnalizadorGastos.gastoPromedioPorDia(mAho);
        assertEquals(100.0, promedio, 0.001);
    }
}
