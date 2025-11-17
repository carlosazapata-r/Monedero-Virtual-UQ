package com.monedero.controller;

import com.monedero.service.OrdenadorTransacciones;
import com.monedero.model.Cliente;
import com.monedero.model.RangoCliente;
import com.monedero.model.monedero.Monedero;
import com.monedero.model.transaccion.Transaccion;
import com.monedero.service.AnalizadorGastos;
import com.monedero.service.GestorPuntos;
import com.monedero.service.SistemaMonedero;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class PrincipalController {

    @FXML
    private ComboBox<Cliente> comboClientes;

    @FXML
    private ComboBox<Monedero> comboMonederos;

    @FXML
    private TextField txtMonto;

    @FXML
    private Label lblSaldo;

    @FXML
    private Label lblPuntos;

    @FXML
    private Label lblRango;

    @FXML
    private TextArea txtHistorial;

    private SistemaMonedero sistema;



    /** Lo llama App o LoginController después de cargar el FXML */
    public void setSistema(SistemaMonedero sistema) {
        this.sistema = sistema;
        if (comboClientes != null) {
            comboClientes.setItems(
                    FXCollections.observableArrayList(sistema.getClientes())
            );
        }
    }

    /** Lo llama LoginController para que el cliente logueado quede seleccionado */
    public void seleccionarCliente(Cliente cliente) {
        if (sistema == null || cliente == null) return;

        comboClientes.setItems(
                FXCollections.observableArrayList(sistema.getClientes())
        );

        comboClientes.getSelectionModel().select(cliente);
        onClienteSeleccionado(); // carga monederos, puntos, etc.
    }

    /**
     * MANEJO DE EVENTOS FXML
     */

    @FXML
    private void onClienteSeleccionado() {
        Cliente c = comboClientes.getValue();
        if (c != null) {
            comboMonederos.setItems(
                    FXCollections.observableArrayList(c.getMonederos())
            );
            actualizarDatosCliente(c);
            lblSaldo.setText("0.0");
            txtHistorial.clear();
        }
    }

    @FXML
    private void onMonederoSeleccionado() {
        Cliente c = comboClientes.getValue();
        Monedero m = comboMonederos.getValue();
        if (c != null && m != null) {
            actualizarMonedero(c, m);
        }
    }

    @FXML
    private void onDepositar() {
        try {
            Cliente c = comboClientes.getValue();
            Monedero m = comboMonederos.getValue();

            if (c == null || m == null) {
                mostrarAlerta("Operación inválida", "Seleccione un cliente y un monedero.");
                return;
            }

            double monto = leerMonto();
            if (monto <= 0) {
                mostrarAlerta("Monto inválido", "El monto debe ser mayor que cero.");
                return;
            }

            sistema.realizarDeposito(c, m, monto);
            actualizarMonedero(c, m);
        } catch (NumberFormatException e) {
            mostrarAlerta("Monto inválido", "Ingrese un número válido.");
        } catch (Exception e) {
            mostrarAlerta("Error al depositar", e.getMessage());
        }
    }

    @FXML
    private void onRetirar() {
        try {
            Cliente c = comboClientes.getValue();
            Monedero m = comboMonederos.getValue();

            if (c == null || m == null) {
                mostrarAlerta("Operación inválida", "Seleccione un cliente y un monedero.");
                return;
            }

            double monto = leerMonto();
            if (monto <= 0) {
                mostrarAlerta("Monto inválido", "El monto debe ser mayor que cero.");
                return;
            }

            sistema.realizarRetiro(c, m, monto);
            actualizarMonedero(c, m);
        } catch (NumberFormatException e) {
            mostrarAlerta("Monto inválido", "Ingrese un número válido.");
        } catch (Exception e) {
            mostrarAlerta("Error al retirar", e.getMessage());
        }
    }

    @FXML
    private void onTransferir() {
        try {
            Cliente c = comboClientes.getValue();
            Monedero origen = comboMonederos.getValue();

            if (c == null || origen == null) {
                mostrarAlerta("Operación inválida", "Seleccione un cliente y un monedero origen.");
                return;
            }

            if (c.getMonederos().size() < 2) {
                mostrarAlerta("Transferencia",
                        "El cliente debe tener al menos dos monederos para transferir.");
                return;
            }

            // monedero destino: el otro monedero del mismo cliente
            Monedero destino = c.getMonederos().stream()
                    .filter(m -> !m.equals(origen))
                    .findFirst()
                    .orElse(null);

            if (destino == null) {
                mostrarAlerta("Transferencia", "No se encontró monedero destino.");
                return;
            }

            double monto = leerMonto();
            if (monto <= 0) {
                mostrarAlerta("Monto inválido", "El monto debe ser mayor que cero.");
                return;
            }

            sistema.realizarTransferencia(c, origen, destino, monto);
            actualizarMonedero(c, origen);
        } catch (NumberFormatException e) {
            mostrarAlerta("Monto inválido", "Ingrese un número válido.");
        } catch (Exception e) {
            mostrarAlerta("Error al transferir", e.getMessage());
        }
    }

    @FXML
    private void onCanjear100() {
        try {
            Cliente c = comboClientes.getValue();
            if (c == null) {
                mostrarAlerta("Canje de puntos", "Seleccione un cliente.");
                return;
            }
            GestorPuntos.canjear(c, 100);
            actualizarDatosCliente(c);
        } catch (Exception e) {
            mostrarAlerta("Error al canjear", e.getMessage());
        }
    }

    @FXML
    private void onVerAnalisis() {
        Cliente c = comboClientes.getValue();
        Monedero m = comboMonederos.getValue();

        if (c == null || m == null) {
            mostrarAlerta("Análisis de gastos", "Seleccione un cliente y un monedero.");
            return;
        }

        double total = AnalizadorGastos.totalGastado(m);
        double promedio = AnalizadorGastos.gastoPromedioPorDia(m);

        mostrarInfo("Análisis de gastos",
                "Total gastado: " + total + "\n" +
                        "Gasto promedio por día: " + promedio);
    }

    /**
     * MÉTODOS AUXILIARES
     * @return
     * @throws NumberFormatException
     */

    private double leerMonto() throws NumberFormatException {
        String texto = txtMonto.getText();
        return Double.parseDouble(texto);
    }

    private void actualizarMonedero(Cliente c, Monedero m) {
        lblSaldo.setText(String.valueOf(m.getSaldo()));
        actualizarDatosCliente(c);

        StringBuilder sb = new StringBuilder();
        for (Transaccion t : m.getHistorial()) {
            sb.append(t.getFecha())
                    .append(" - ")
                    .append(t.getDescripcion())
                    .append("\n");
        }
        txtHistorial.setText(sb.toString());
    }

    private void actualizarDatosCliente(Cliente c) {
        lblPuntos.setText(String.valueOf(c.getPuntos()));
        RangoCliente rango = GestorPuntos.calcularRango(c);
        lblRango.setText(rango.name());
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setTitle(titulo);
        a.showAndWait();
    }

    private void mostrarInfo(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setTitle(titulo);
        a.showAndWait();
    }

    @FXML
    private void onOrdenarHistorial() {
        Cliente c = comboClientes.getValue();
        Monedero m = comboMonederos.getValue();

        if (c == null || m == null) {
            mostrarAlerta("Ordenar historial", "Seleccione un cliente y un monedero.");
            return;
        }

        var listaOrdenada = OrdenadorTransacciones.ordenarPorFecha(m);

        StringBuilder sb = new StringBuilder();
        for (Transaccion t : listaOrdenada) {
            sb.append(t.getFecha())
                    .append(" - ")
                    .append(t.getDescripcion())
                    .append("\n");
        }
        txtHistorial.setText(sb.toString());
    }

}
