package com.monedero.controller;

import com.monedero.App;
import com.monedero.model.Cliente;
import com.monedero.model.monedero.MonederoAhorros;
import com.monedero.model.monedero.MonederoGastos;
import com.monedero.service.SistemaMonedero;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.monedero.controller.PrincipalController;
import java.util.UUID;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    private SistemaMonedero sistema;
    private Stage stage;

    // Lo llama App después de cargar el FXML
    public void init(SistemaMonedero sistema, Stage stage) {
        this.sistema = sistema;
        this.stage = stage;
    }

    @FXML
    private void onLogin() {
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();

        if (usuario == null || usuario.isBlank() ||
                password == null || password.isBlank()) {
            mostrarAlerta("Datos incompletos",
                    "Debe ingresar usuario y contraseña.");
            return;
        }

        Cliente cliente = sistema.buscarClientePorNombre(usuario);

        if (cliente == null) {

            //registrar nuevo cliente
            String id = UUID.randomUUID().toString(); // id interno cualquiera
            cliente = new Cliente(id, usuario, password);

            // Crear monederos por defecto
            MonederoAhorros mAho = new MonederoAhorros("AHO-" + usuario);
            MonederoGastos mGas = new MonederoGastos("GAS-" + usuario);

            cliente.agregarMonedero(mAho);
            cliente.agregarMonedero(mGas);

            sistema.registrarCliente(cliente);
        } else {
            // Cliente ya existe: validar contraseña
            if (!cliente.getPassword().equals(password)) {
                mostrarAlerta("Error de login",
                        "Contraseña incorrecta para el usuario " + usuario);
                return;
            }
        }

        // Abrir la pantalla principal con ese cliente
        abrirPantallaPrincipal(cliente);
    }

    private void abrirPantallaPrincipal(Cliente clienteActual) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/vista_principal.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);

            PrincipalController principalController = loader.getController();
            principalController.setSistema(sistema);
            principalController.seleccionarCliente(clienteActual);
            principalController.setStage(stage);

            stage.setTitle("Monedero Virtual - Cliente: " + clienteActual.getNombre());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error",
                    "Error al cargar la pantalla principal: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alerta.setTitle(titulo);
        alerta.showAndWait();
    }
}
