// c:\2025 POO I - Trabajo Integrador\gestion-eventos-culturales\src\main\java\com\gestioneventos\controller\MainController.java
package com.gestioneventos.controller;

import com.gestioneventos.model.eventos.Evento;
import com.gestioneventos.repositorio.RepositorioEvento;
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
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;
import java.time.LocalDate;
import java.time.YearMonth;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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

        RepositorioEvento repo = new RepositorioEvento();
        List<Evento> eventosMes = repo.buscarTodos().stream()
            .filter(e -> e.getFechaInicio().getMonthValue() == yearMonth.getMonthValue() && e.getFechaInicio().getYear() == yearMonth.getYear())
            .collect(Collectors.toList());

        int col = dayOfWeek - 1;
        int row = 0;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = yearMonth.atDay(day);
            // Buscar eventos activos en este día
            List<Evento> eventosDia = eventosMes.stream()
                .filter(e -> {
                    LocalDate inicio = e.getFechaInicio();
                    LocalDate fin = inicio.plusDays(e.getDuracionEstimada() - 1);
                    return !date.isBefore(inicio) && !date.isAfter(fin);
                })
                .collect(Collectors.toList());

            StackPane dayPane = new StackPane();
            dayPane.setMinSize(28, 28);
            dayPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            dayPane.setStyle("-fx-alignment: center;");

            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setStyle("-fx-font-size: 14px; -fx-text-alignment: center; -fx-alignment: center;");
            if (date.equals(today)) {
                dayLabel.setStyle(dayLabel.getStyle() + "-fx-background-color: #5bc0be; -fx-text-fill: white; -fx-background-radius: 20px;");
            }

            // Línea a la izquierda si hay eventos activos en este día
            if (!eventosDia.isEmpty()) {
                Color lineColor = date.isBefore(today) ? Color.RED : Color.web("#5bc0be");
                Line leftLine = new Line(0, 0, 0, 18); // Altura ajustable
                leftLine.setStroke(lineColor);
                leftLine.setStrokeWidth(4);
                leftLine.setTranslateX(-12);
                dayPane.getChildren().add(leftLine);
            }
            dayPane.getChildren().add(dayLabel);

            // Acción al hacer click: mostrar eventos activos en el día
            dayPane.setOnMouseClicked(e -> {
                if (!eventosDia.isEmpty()) {
                    String eventosStr = eventosDia.stream()
                        .map(ev -> ev.getNombre())
                        .collect(Collectors.joining("\n"));
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                    alert.setTitle("Eventos del día");
                    alert.setHeaderText("Eventos para el " + date);
                    alert.setContentText(eventosStr);
                    alert.showAndWait();
                }
            });

            calendarGrid.add(dayPane, col, row);
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