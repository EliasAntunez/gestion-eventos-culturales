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
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import java.time.LocalDate;
import java.time.YearMonth;
import java.io.IOException;

/**
 * Controlador principal de la aplicación.
 * Maneja la navegación entre las diferentes vistas.
 */
public class MainController {

    // === Referencias a los nodos del FXML ===
    @FXML private Label monthYearLabel;
    @FXML private GridPane calendarGrid;
    @FXML private ListView<String> upcomingEventsList;
    @FXML private Label eventsCountLabel;
    @FXML private Label participantsCountLabel;

    private YearMonth currentYearMonth;

    // === Inicialización ===
    @FXML
    public void initialize() {
        // Inicializa el calendario en el mes actual
        currentYearMonth = YearMonth.now();
        showCalendar(currentYearMonth);
    }

    // === Lógica del calendario ===
    private void showCalendar(YearMonth yearMonth) {
        calendarGrid.getChildren().clear();
        LocalDate today = LocalDate.now();
        monthYearLabel.setText(
            yearMonth.getMonth().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.getDefault())
            + " " + yearMonth.getYear()
        );

        LocalDate firstOfMonth = yearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue(); // 1 = lunes, 7 = domingo
        int daysInMonth = yearMonth.lengthOfMonth();

        int col = dayOfWeek - 1;
        int row = 0;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = yearMonth.atDay(day);
            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setMinSize(28, 28);
            dayLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            dayLabel.setStyle("-fx-alignment: center; -fx-font-size: 14px; -fx-text-alignment: center; -fx-alignment: center;");

            // Resalta el día actual
            if (date.equals(today)) {
                dayLabel.setStyle(dayLabel.getStyle() +
                "-fx-background-color: #5bc0be; -fx-text-fill: white; -fx-background-radius: 20px;");
            }

            StackPane cell = new StackPane(dayLabel);
            calendarGrid.add(cell, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    @FXML
    private void prevMonth(ActionEvent event) {
        currentYearMonth = currentYearMonth.minusMonths(1);
        showCalendar(currentYearMonth);
    }

    @FXML
    private void nextMonth(ActionEvent event) {
        currentYearMonth = currentYearMonth.plusMonths(1);
        showCalendar(currentYearMonth);
    }

    // === Menú y navegación ===
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/eventos/ListaEventoView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Gestión de Eventos");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Opcional: bloquea la ventana principal
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al abrir la gestión de eventos", e.getMessage());
        }
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