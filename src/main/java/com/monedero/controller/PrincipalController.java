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
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import java.time.LocalDate;
import com.monedero.model.transaccion.TransaccionProgramada;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

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

    @FXML
    private ComboBox<String> comboTipoProgramada;

    @FXML
    private DatePicker datePickerProgramada;

    @FXML
    private CheckBox checkRecurrente;

    @FXML
    private Label lblEstado;

    @FXML
    private Label lblBienvenido;


    private SistemaMonedero sistema;
    private Cliente clienteActual;

    private Stage stage;

    /** Lo llama App o LoginController después de cargar el FXML */
    public void setSistema(SistemaMonedero sistema) {
        this.sistema = sistema;

        // Clientes destino (para transferencias entre clientes)
        if (comboClientesDestino != null) {
            comboClientesDestino.setItems(
                    FXCollections.observableArrayList(sistema.getClientes())
            );
        }

        // Tipos de transacción programada
        if (comboTipoProgramada != null) {
            comboTipoProgramada.setItems(
                    FXCollections.observableArrayList("DEPOSITO", "TRANSFERENCIA")
            );
        }

        // Cómo se muestran los monederos en los combos
        configurarComboMonedero(comboMonederos);
        configurarComboMonedero(comboMonederosDestino);
    }



    /** Lo llama LoginController para que el cliente logueado quede seleccionado */
    public void seleccionarCliente(Cliente cliente) {
        if (sistema == null || cliente == null) return;

        // Guardamos el cliente logueado
        this.clienteActual = cliente;

        // (comboClientes es oculto pero lo mantenemos sincronizado por si algo lo usa)
        if (comboClientes != null) {
            comboClientes.setItems(FXCollections.observableArrayList(cliente));
            comboClientes.getSelectionModel().selectFirst();
        }

        // Mensaje de bienvenida
        if (lblBienvenido != null) {
            lblBienvenido.setText("Bienvenido, " + cliente.getNombre());
        }

        // Cargamos los monederos de ese cliente para el combo de origen
        if (comboMonederos != null) {
            comboMonederos.setItems(
                    FXCollections.observableArrayList(cliente.getMonederos())
            );
            comboMonederos.getSelectionModel().clearSelection();
        }

        // Puntos y rango
        actualizarDatosCliente(cliente);

        // Reseteamos saldo visible e historial hasta que el usuario elija un monedero
        if (lblSaldo != null) {
            lblSaldo.setText("$ 0.00");
        }
        if (txtHistorial != null) {
            txtHistorial.clear();
        }
    }



    @FXML
    private void onClienteSeleccionado() {
    }

    @FXML
    private void onMonederoSeleccionado() {
        Monedero m = comboMonederos.getValue();
        if (clienteActual != null && m != null) {
            actualizarMonedero(clienteActual, m);
        }
    }

    @FXML
    private void onDepositar() {
        try {
            Cliente c = clienteActual;
            Monedero m = comboMonederos.getValue();

            if (c == null || m == null) {
                mostrarAlerta("Operación inválida", "Seleccione un cliente y un monedero.");
                mostrarEstadoError("Seleccione cliente y monedero antes de depositar.");
                return;
            }

            double monto = leerMonto();
            if (monto <= 0) {
                mostrarAlerta("Monto inválido", "El monto debe ser mayor que cero.");
                mostrarEstadoError("El monto debe ser mayor que cero.");
                return;
            }

            sistema.realizarDeposito(c, m, monto);
            actualizarMonedero(c, m);
            limpiarCampos();

            mostrarEstadoOk("Depósito de $ " + String.format("%.2f", monto) + " realizado correctamente.");

        } catch (NumberFormatException e) {
            mostrarAlerta("Monto inválido", "Ingrese un número válido.");
            mostrarEstadoError("Error: formato de monto inválido.");
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
            mostrarEstadoError("Error al depositar: " + e.getMessage());
        }
    }

    @FXML
    private void onRetirar() {
        try {
            Cliente cliente = clienteActual;
            Monedero monedero = comboMonederos.getValue();

            if (cliente == null || monedero == null) {
                mostrarAlerta("Operación inválida", "Seleccione un cliente y un monedero.");
                mostrarEstadoError("Seleccione cliente y monedero antes de retirar.");
                return;
            }

            double monto = leerMonto();
            if (monto <= 0) {
                mostrarAlerta("Monto inválido", "El monto debe ser mayor que cero.");
                mostrarEstadoError("El monto debe ser mayor que cero.");
                return;
            }

            sistema.realizarRetiro(cliente, monedero, monto);
            actualizarMonedero(cliente, monedero);
            actualizarDatosCliente(cliente);
            limpiarCampos();

            mostrarEstadoOk("Retiro de $ " + String.format("%.2f", monto) + " realizado correctamente.");

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Ingrese un monto válido.");
            mostrarEstadoError("Monto inválido.");
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
            mostrarEstadoError("Error al retirar: " + e.getMessage());
        }
    }

    @FXML
    private void onTransferir() {
        try {
            Cliente clienteOrigen = clienteActual;
            Monedero monederoOrigen = comboMonederos.getValue();
            Cliente clienteDestino = comboClientesDestino.getValue();
            Monedero monederoDestino = comboMonederosDestino.getValue();

            if (clienteOrigen == null || monederoOrigen == null) {
                mostrarAlerta("Operación inválida", "Seleccione cliente y monedero origen.");
                mostrarEstadoError("Seleccione cliente y monedero origen antes de transferir.");
                return;
            }

            if (clienteDestino == null || monederoDestino == null) {
                mostrarAlerta("Operación inválida", "Seleccione cliente y monedero destino.");
                mostrarEstadoError("Seleccione cliente y monedero destino antes de transferir.");
                return;
            }

            if (clienteOrigen == clienteDestino && monederoOrigen == monederoDestino) {
                mostrarAlerta("Operación inválida", "No puede transferirse a sí mismo.");
                mostrarEstadoError("Transferencia inválida (origen y destino iguales).");
                return;
            }

            double monto = leerMonto();
            if (monto <= 0) {
                mostrarAlerta("Monto inválido", "El monto debe ser mayor que cero.");
                mostrarEstadoError("Monto inválido para transferir.");
                return;
            }

            sistema.realizarTransferencia(
                    clienteOrigen, monederoOrigen,
                    clienteDestino, monederoDestino,
                    monto
            );


            actualizarMonedero(clienteOrigen, monederoOrigen);
            actualizarDatosCliente(clienteOrigen);

            limpiarCampos();

            mostrarEstadoOk("Transferencia de $ " + String.format("%.2f", monto) + " realizada correctamente.");

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Ingrese un monto válido.");
            mostrarEstadoError("Monto inválido.");
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
            mostrarEstadoError("Error al transferir: " + e.getMessage());
        }
    }

    @FXML
    private void onCanjear100() {
        try {
            Cliente c = clienteActual;
            if (c == null) {
                mostrarAlerta("Canje de puntos", "Seleccione un cliente.");
                return;
            }

            GestorPuntos.canjear(c, 100);
            actualizarDatosCliente(c);

            mostrarInfo("Canje de 100 puntos",
                    "Beneficio activado: 10% de descuento en la comisión de tu próxima transferencia.");
        } catch (Exception e) {
            mostrarAlerta("Canje de puntos", e.getMessage());
        }
    }

    @FXML
    private void onCanjear500() {
        try {
            Cliente c = clienteActual;
            if (c == null) {
                mostrarAlerta("Canje de puntos", "Seleccione un cliente.");
                return;
            }

            GestorPuntos.canjear(c, 500);
            actualizarDatosCliente(c);

            mostrarInfo("Canje de 500 puntos",
                    "Beneficio activado: Retiros sin cargo durante 1 mes.");
        } catch (Exception e) {
            mostrarAlerta("Canje de puntos", e.getMessage());
        }
    }

    @FXML
    private void onCanjear1000() {
        try {
            Cliente c = clienteActual;
            Monedero m = comboMonederos.getValue();

            if (c == null || m == null) {
                mostrarAlerta("Canje de puntos", "Seleccione un cliente y un monedero para recibir el bono.");
                return;
            }

            GestorPuntos.canjear(c, 1000);

            // Aplicamos bono de 50 unidades al monedero seleccionado
            m.acreditar(50, null);
            actualizarMonedero(c, m);
            actualizarDatosCliente(c);

            mostrarInfo("Canje de 1000 puntos",
                    "Beneficio activado: Has recibido un bono de 50 unidades en este monedero.");
        } catch (Exception e) {
            mostrarAlerta("Canje de puntos", e.getMessage());
        }
    }

    @FXML
    private void onVerAnalisis() {
        Cliente c = clienteActual;
        Monedero m = comboMonederos.getValue();

        if (c == null || m == null) {
            mostrarAlerta("Análisis de gastos", "Seleccione un cliente y un monedero.");
            return;
        }

        double total = AnalizadorGastos.totalGastado(m);
        double promedio = AnalizadorGastos.gastoPromedioPorDia(m);

        mostrarInfo("Análisis de gastos",
                "Total gastado: $ " + String.format("%.2f", total) + "\n" +
                        "Gasto promedio por día: $ " + String.format("%.2f", promedio));
    }

    /**
     * MÉTODOS AUXILIARES
     */

    private double leerMonto() throws NumberFormatException {
        String texto = txtMonto.getText();
        return Double.parseDouble(texto);
    }

    private void actualizarMonedero(Cliente c, Monedero m) {
        if (c == null || m == null) return;

        lblSaldo.setText(String.format("$ %.2f", m.getSaldo()));

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
        Cliente c = clienteActual;
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

    private void limpiarCampos() {
        // Limpia el monto
        txtMonto.clear();

        // Limpia combos de destino
        if (comboClientesDestino != null) {
            comboClientesDestino.getSelectionModel().clearSelection();
        }
        if (comboMonederosDestino != null) {
            comboMonederosDestino.getSelectionModel().clearSelection();
            comboMonederosDestino.getItems().clear();
        }
        if (txtMonto.getParent() != null) {
            txtMonto.getParent().requestFocus();
        }
    }

    @FXML
    private void onProgramarTransaccion() {
        try {
            Cliente clienteOrigen = clienteActual;
            Monedero monederoOrigen = comboMonederos.getValue();
            Cliente clienteDestino = comboClientesDestino.getValue();
            Monedero monederoDestino = comboMonederosDestino.getValue();
            String tipo = comboTipoProgramada.getValue();
            LocalDate fecha = datePickerProgramada.getValue();
            double monto = leerMonto();

            if (clienteOrigen == null || monederoOrigen == null) {
                mostrarAlerta("Transacción programada", "Seleccione cliente y monedero origen.");
                return;
            }

            if (tipo == null || fecha == null) {
                mostrarAlerta("Transacción programada", "Seleccione tipo y fecha de ejecución.");
                return;
            }

            if ("TRANSFERENCIA".equals(tipo)) {
                if (clienteDestino == null || monederoDestino == null) {
                    mostrarAlerta("Transacción programada",
                            "Para transferencias, seleccione cliente y monedero destino.");
                    return;
                }
            }

            TransaccionProgramada.Tipo tipoEnum =
                    "DEPOSITO".equals(tipo)
                            ? TransaccionProgramada.Tipo.DEPOSITO
                            : TransaccionProgramada.Tipo.TRANSFERENCIA;

            TransaccionProgramada t = new TransaccionProgramada(
                    tipoEnum,
                    clienteOrigen,
                    monederoOrigen,
                    clienteDestino,
                    monederoDestino,
                    monto,
                    fecha,
                    checkRecurrente.isSelected(),
                    30 // se repite cada 30 días si es recurrente
            );

            sistema.programarTransaccion(t);
            mostrarInfo("Transacción programada", "La transacción fue agendada correctamente.");

            limpiarCampos();

        } catch (NumberFormatException e) {
            mostrarAlerta("Monto inválido", "Ingrese un número válido.");
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    @FXML
    private void onProcesarTransacciones() {
        sistema.procesarTransaccionesProgramadas();
        mostrarInfo("Transacciones programadas", "Se han procesado las transacciones pendientes.");
    }

    private void mostrarEstadoOk(String mensaje) {
        if (lblEstado != null) {
            lblEstado.setText(mensaje);
            lblEstado.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 12px;");
        }
    }

    private void mostrarEstadoError(String mensaje) {
        if (lblEstado != null) {
            lblEstado.setText(mensaje);
            lblEstado.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");
        }
    }

    /** ---------- Configuración visual de los ComboBox de Monedero ---------- */

    private void configurarComboMonedero(ComboBox<Monedero> combo) {
        if (combo == null) return;

        combo.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Monedero item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(textoMonedero(item));
                }
            }
        });

        combo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Monedero item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(textoMonedero(item));
                }
            }
        });
    }

    private String textoMonedero(Monedero m) {
        String tipo;
        String simpleName = m.getClass().getSimpleName();
        if (simpleName.contains("Ahorros")) {
            tipo = "Ahorros";
        } else {
            tipo = "Gastos Diarios";
        }
        return tipo + " - $ " + String.format("%.2f", m.getSaldo());
    }
}
