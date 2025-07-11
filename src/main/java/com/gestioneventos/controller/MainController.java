// c:\2025 POO I - Trabajo Integrador\gestion-eventos-culturales\src\main\java\com\gestioneventos\controller\MainController.java
package com.gestioneventos.controller;

import com.gestioneventos.model.eventos.EstadoEvento;
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
        // Actualizar automáticamente el estado de los eventos
        actualizarEstadosEventos();
        
        calendarGrid.getChildren().clear();
        LocalDate today = LocalDate.now();
        monthYearLabel.setText(
            yearMonth.getMonth().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.getDefault())
            + " " + yearMonth.getYear()
        );

        LocalDate firstOfMonth = yearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue(); // 1 = lunes, 7 = domingo
        int daysInMonth = yearMonth.lengthOfMonth();

        // Obtener todos los eventos del mes actual
        RepositorioEvento repositorioEvento = new RepositorioEvento();
        List<Evento> eventosMes = repositorioEvento.buscarTodos().stream()
            .filter(e -> {
                LocalDate fechaInicio = e.getFechaInicio();
                LocalDate fechaFin = fechaInicio.plusDays(e.getDuracionEstimada() - 1);
                // Incluimos eventos que empiezan, terminan o transcurren durante este mes
                return (fechaInicio.getYear() == yearMonth.getYear() && fechaInicio.getMonthValue() == yearMonth.getMonthValue())
                    || (fechaFin.getYear() == yearMonth.getYear() && fechaFin.getMonthValue() == yearMonth.getMonthValue())
                    || (fechaInicio.isBefore(yearMonth.atDay(1)) && fechaFin.isAfter(yearMonth.atEndOfMonth()));
            })
            .collect(Collectors.toList());

        int col = dayOfWeek - 1;
        int row = 0;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = yearMonth.atDay(day);
            
            // Buscar eventos activos en este día
            List<Evento> eventosDia = eventosMes.stream()
                .filter(e -> {
                    LocalDate inicio = e.getFechaInicio();
                    LocalDate fechaFin = inicio.plusDays(e.getDuracionEstimada() - 1);
                    return !date.isBefore(inicio) && !date.isAfter(fechaFin);
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
                Color lineColor = Color.web("#5bc0be");
                Line leftLine = new Line(0, 0, 0, 18);
                leftLine.setStroke(lineColor);
                leftLine.setStrokeWidth(4);
                leftLine.setTranslateX(-12);
                dayPane.getChildren().add(leftLine);
            }
            dayPane.getChildren().add(dayLabel);

            // Acción al hacer click: mostrar eventos activos en el día
            final LocalDate finalDate = date;
            final List<Evento> finalEventosDia = eventosDia;
            dayPane.setOnMouseClicked(e -> {
                if (!finalEventosDia.isEmpty()) {
                    StringBuilder contenido = new StringBuilder();
                    for (Evento evento : finalEventosDia) {
                        contenido.append("• ").append(evento.getNombre())
                                .append(" (").append(evento.getEstadoEvento()).append(")\n");
                    }
                    
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                    alert.setTitle("Eventos del día");
                    alert.setHeaderText("Eventos para el " + finalDate);
                    alert.setContentText(contenido.toString());
                    alert.showAndWait();
                }
            });

            // Cambiar cursor a mano si hay eventos
            if (!eventosDia.isEmpty()) {
                dayPane.setCursor(javafx.scene.Cursor.HAND);
            }

            calendarGrid.add(dayPane, col, row);
            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * Actualiza automáticamente el estado de los eventos según la fecha actual.
     * - Eventos cuya fecha fin (fechaInicio + duracionEstimada - 1) es anterior a hoy se marcan como FINALIZADOS
     * - Eventos cuya fecha inicio es hoy o anterior y fecha fin es posterior a hoy se marcan como EN_CURSO
     */
    private void actualizarEstadosEventos() {
        RepositorioEvento repo = new RepositorioEvento();
        LocalDate hoy = LocalDate.now();
        
        // Obtener todos los eventos que no estén ya FINALIZADOS o CANCELADOS
        List<Evento> eventos = repo.buscarTodos().stream()
            .filter(e -> e.getEstadoEvento() != EstadoEvento.FINALIZADO && 
                        e.getEstadoEvento() != EstadoEvento.CANCELADO)
            .collect(Collectors.toList());
        
        for (Evento evento : eventos) {
            LocalDate fechaInicio = evento.getFechaInicio();
            LocalDate fechaFin = fechaInicio.plusDays(evento.getDuracionEstimada() - 1);
            
            // Si la fecha fin ya pasó, marcar como FINALIZADO
            if (fechaFin.isBefore(hoy)) {
                evento.setEstadoEvento(EstadoEvento.FINALIZADO);
                repo.actualizar(evento);
                System.out.println("Evento finalizado automáticamente: " + evento.getNombre());
            }
            // Si ya inició pero no terminó, marcar como EN_CURSO (solo si está CONFIRMADO)
            else if (evento.getEstadoEvento() == EstadoEvento.CONFIRMADO && 
                    !fechaInicio.isAfter(hoy) && fechaFin.isAfter(hoy)) {
                evento.setEstadoEvento(EstadoEvento.EN_EJECUCION);
                repo.actualizar(evento);
                System.out.println("Evento marcado en curso automáticamente: " + evento.getNombre());
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