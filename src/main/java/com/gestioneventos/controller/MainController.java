// c:\2025 POO I - Trabajo Integrador\gestion-eventos-culturales\src\main\java\com\gestioneventos\controller\MainController.java
package com.gestioneventos.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador principal de la aplicación.
 * Maneja la navegación entre las diferentes vistas.
 */
public class MainController {

    /**
     * Cierra la aplicación.
     */
    @FXML
    private void salir(ActionEvent event) {
        Platform.exit();
    }

    /**
     * Abre el formulario para crear un nuevo evento.
     */
    @FXML
    private void nuevoEvento(ActionEvent event) {
        // Implementar la lógica para crear un nuevo evento
        System.out.println("Crear nuevo evento");
    }

    /**
     * Abre la vista para gestionar eventos existentes.
     */
    @FXML
    private void gestionarEventos(ActionEvent event) {
        // Implementar la lógica para gestionar eventos
        System.out.println("Gestionar eventos");
    }

    /**
     * Abre el formulario para crear una nueva persona.
     */
    @FXML
    private void nuevaPersona(ActionEvent event) {
        try {
            // Cargar la vista del formulario de persona
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/personas/FormularioPersonaView.fxml"));
            Parent root = loader.load();
            
            // Configurar y mostrar la ventana
            Stage stage = new Stage();
            stage.setTitle("Nueva Persona");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Modal para bloquear ventana principal
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al abrir el formulario de personas", e.getMessage());
        }
    }

    /**
     * Abre la vista para gestionar personas existentes.
     */
    @FXML
    private void gestionarPersonas(ActionEvent event) {
        try {
            // Cargar la vista de lista de personas
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/personas/ListaPersonasView.fxml"));
            Parent root = loader.load();
            
            // Configurar y mostrar la ventana
            Stage stage = new Stage();
            stage.setTitle("Gestión de Personas");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al abrir la gestión de personas", e.getMessage());
        }
    }
    
    /**
     * Muestra un diálogo de error al usuario.
     */
    private void mostrarError(String titulo, String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}