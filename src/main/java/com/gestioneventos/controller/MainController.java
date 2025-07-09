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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;

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

    @FXML
    private void nuevoEvento(ActionEvent event) {
        // Implementar la lógica para crear un nuevo evento
        System.out.println("Crear nuevo evento");
    }

    @FXML
private void gestionarEventos(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/eventos/ListaEventoView.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Gestión de Eventos");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    } catch (IOException e) {
        e.printStackTrace();
        mostrarError("Error al abrir la gestión de eventos", e.getMessage());
    }
}

    @FXML
    private void gestionarPersonas(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/personas/ListaPersonasView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Gestión de Personas");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al abrir la gestión de personas", e.getMessage());
        }
    }

    // === Utilidad ===
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
}