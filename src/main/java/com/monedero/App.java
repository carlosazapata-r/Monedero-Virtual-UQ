package com.monedero;

import com.monedero.controller.LoginController;
import com.monedero.notificacion.NotificadorEmail;
import com.monedero.service.SistemaMonedero;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private SistemaMonedero sistema;

    @Override
    public void start(Stage stage) throws Exception {
        // Sistema vacío, sin clientes hardcodeados
        sistema = new SistemaMonedero(new NotificadorEmail());

        // Cargar LOGIN primero
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/login.fxml"));
        Scene scene = new Scene(loader.load(), 400, 250);

        // Pasar el sistema y el stage al login
        LoginController controller = loader.getController();
        controller.init(sistema, stage);

        stage.setTitle("Login - Monedero Virtual");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
