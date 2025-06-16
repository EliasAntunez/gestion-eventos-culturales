package com.gestioneventos.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MainController {

    @FXML
    private void salir(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void nuevoEvento(ActionEvent event) {
        // Implementar la lógica para crear un nuevo evento
        System.out.println("Crear nuevo evento");
    }

    @FXML
    private void gestionarEventos(ActionEvent event) {
        // Implementar la lógica para gestionar eventos
        System.out.println("Gestionar eventos");
    }

    @FXML
    private void nuevaPersona(ActionEvent event) {
        // Implementar la lógica para crear una nueva persona
        System.out.println("Crear nueva persona");
    }

    @FXML
    private void gestionarPersonas(ActionEvent event) {
        // Implementar la lógica para gestionar personas
        System.out.println("Gestionar personas");
    }
}