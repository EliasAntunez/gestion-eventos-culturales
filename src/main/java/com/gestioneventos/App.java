package com.gestioneventos;

import com.gestioneventos.util.JPAUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainView.fxml"));
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/css/styles-main.css").toExternalForm());
            primaryStage.setTitle("Gestión de Eventos Culturales");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    @Override
    public void stop() {
        // Cerrar EntityManagerFactory al salir
        JPAUtil.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}