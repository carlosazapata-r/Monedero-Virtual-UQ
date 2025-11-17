package com.monedero.controller;

import javafx.stage.Stage;
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
import com.monedero.App;
import com.monedero.controller.LoginController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;


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

    @FXML
    private ComboBox<Cliente> comboClientesDestino;

    @FXML
    private ComboBox<Monedero> comboMonederosDestino;


    private SistemaMonedero sistema;



    /** Lo llama App o LoginController después de cargar el FXML */
    public void setSistema(SistemaMonedero sistema) {
        this.sistema = sistema;
        if (comboClientes != null) {
            comboClientes.setItems(
                    FXCollections.observableArrayList(sistema.getClientes())
            );
        }
        if (comboClientesDestino != null) {
            comboClientesDestino.setItems(
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
            Cliente clienteOrigen = comboClientes.getValue();
            Monedero monederoOrigen = comboMonederos.getValue();

            Cliente clienteDestino = comboClientesDestino.getValue();
            Monedero monederoDestino = comboMonederosDestino.getValue();

            if (clienteOrigen == null || monederoOrigen == null) {
                mostrarAlerta("Transferencia", "Seleccione cliente y monedero ORIGEN.");
                return;
            }

            if (clienteDestino == null || monederoDestino == null) {
                mostrarAlerta("Transferencia", "Seleccione cliente y monedero DESTINO.");
                return;
            }

            if (clienteOrigen == clienteDestino && monederoOrigen == monederoDestino) {
                mostrarAlerta("Transferencia", "El origen y el destino no pueden ser el mismo.");
                return;
            }

            double monto = leerMonto();
            if (monto <= 0) {
                mostrarAlerta("Monto inválido", "El monto debe ser mayor que cero.");
                return;
            }

            sistema.realizarTransferencia(
                    clienteOrigen, monederoOrigen,
                    clienteDestino, monederoDestino,
                    monto
            );

            actualizarMonedero(clienteOrigen, monederoOrigen);
            txtMonto.clear();
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

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void onLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/login.fxml"));
            Scene scene = new Scene(loader.load(), 400, 250);

            // Volvemos a inicializar el LoginController con el mismo sistema y stage
            LoginController loginController = loader.getController();
            loginController.init(sistema, stage);

            stage.setTitle("Login - Monedero Virtual");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo volver al login: " + e.getMessage());
        }
    }

    @FXML
    private void onClienteDestinoSeleccionado() {
        Cliente c = comboClientesDestino.getValue();
        if (c != null) {
            comboMonederosDestino.setItems(
                    FXCollections.observableArrayList(c.getMonederos())
            );
        } else {
            comboMonederosDestino.getItems().clear();
        }
    }



}
