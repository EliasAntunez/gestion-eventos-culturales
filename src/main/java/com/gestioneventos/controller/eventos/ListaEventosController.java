package com.gestioneventos.controller.eventos;

import com.gestioneventos.model.eventos.Evento;
import com.gestioneventos.controller.participaciones.ListaParticipacionesController;
import com.gestioneventos.model.eventos.Cine;
import com.gestioneventos.model.eventos.Taller;
import com.gestioneventos.model.eventos.Concierto;
import com.gestioneventos.model.eventos.Exposicion;
import com.gestioneventos.model.eventos.Feria;
import com.gestioneventos.model.eventos.EstadoEvento;
import com.gestioneventos.service.EventoService;
import com.gestioneventos.service.impl.EventoServiceImpl;
import com.gestioneventos.util.DateUtils;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
//import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controlador para la vista de listado de eventos.
 * Permite visualizar, filtrar, crear, editar y eliminar eventos.
 */
public class ListaEventosController implements Initializable {

    // Elementos de la interfaz
    @FXML private TableView<Evento> tablaEventos;
    @FXML private TableColumn<Evento, Long> colId;
    @FXML private TableColumn<Evento, String> colNombre;
    @FXML private TableColumn<Evento, String> colTipoEvento;
    @FXML private TableColumn<Evento, String> colFechaInicio;
    @FXML private TableColumn<Evento, Integer> colDuracion;
    @FXML private TableColumn<Evento, String> colEstado;
    @FXML private TableColumn<Evento, String> colDetalle;
    @FXML private TableColumn<Evento, String> colPermiteInscripcion;
    
    // Elementos de filtrado
    @FXML private TextField txtFiltroNombre;
    @FXML private ComboBox<EstadoEvento> cmbFiltroEstado;
    @FXML private DatePicker dpFiltroFechaDesde;
    @FXML private DatePicker dpFiltroFechaHasta;
    @FXML private ComboBox<String> cmbFiltroTipo;
    
    // Acciones
    @FXML private Button btnNuevoEvento;
    @FXML private Button btnEditarEvento;
    @FXML private Button btnEliminarEvento;
    @FXML private Button btnVerParticipantes;
    
    // Servicio
    private final EventoService eventoService;
    
    // Lista de eventos observable para la tabla
    private ObservableList<Evento> eventosObservable;

    /**
     * Constructor que inicializa el servicio.
     */
    public ListaEventosController() {
        this.eventoService = new EventoServiceImpl();
        this.eventosObservable = FXCollections.observableArrayList();
    }

    /**
     * Inicializa la vista.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar columnas de la tabla
        configurarColumnas();
        
        // Inicializar filtros
        inicializarFiltros();
        
        // Configurar selección de la tabla
        configurarSeleccionTabla();
        // Deshabilitar botones al inicio
        btnEditarEvento.setDisable(true);
        btnEliminarEvento.setDisable(true);
        btnVerParticipantes.setDisable(true);

        // Cargar datos iniciales
        cargarEventos();
    }
    
    /**
     * Configura las columnas de la tabla.
     */
    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        
        // Columna tipo de evento (determinada por la clase)
        colTipoEvento.setCellValueFactory(cellData -> {
            Evento evento = cellData.getValue();
            String tipoEvento = "Desconocido";
            
            if (evento instanceof Cine) tipoEvento = "Cine";
            else if (evento instanceof Taller) tipoEvento = "Taller";
            else if (evento instanceof Concierto) tipoEvento = "Concierto";
            else if (evento instanceof Exposicion) tipoEvento = "Exposición";
            else if (evento instanceof Feria) tipoEvento = "Feria";
            
            return new SimpleStringProperty(tipoEvento);
        });
        
        // Formato de fecha
        colFechaInicio.setCellValueFactory(cellData -> {
            LocalDate fecha = cellData.getValue().getFechaInicio();
            return new SimpleStringProperty(DateUtils.formatLocalDate(fecha));
        });
        
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracionEstimada"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoEvento"));
        
        // Columna permite inscripción
        colPermiteInscripcion.setCellValueFactory(cellData -> {
            boolean permiteInscripcion = cellData.getValue().getPermiteInscripcion();
            return new SimpleStringProperty(permiteInscripcion ? "Sí" : "No");
        });
        
        // Columna de detalle específico
        colDetalle.setCellValueFactory(cellData -> {
            Evento evento = cellData.getValue();
            String detalle = "";
            
            if (evento instanceof Cine) {
                Cine cine = (Cine) evento;
                detalle = "Película: " + cine.getTituloPelicula();
            } else if (evento instanceof Taller) {
                Taller taller = (Taller) evento;
                detalle = "Cupo: " + taller.getCupoMaximo() + ", " + taller.getModalidad();
            } else if (evento instanceof Concierto) {
                Concierto concierto = (Concierto) evento;
                detalle = "Artista: " + concierto.getArtistaPrincipal();
            } else if (evento instanceof Exposicion) {
                Exposicion exposicion = (Exposicion) evento;
                detalle = "Arte: " + exposicion.getTipoArte();
            } else if (evento instanceof Feria) {
                Feria feria = (Feria) evento;
                detalle = "Stands: " + feria.getCantidadStands();
            }
            
            return new SimpleStringProperty(detalle);
        });
    }
    
    /**
     * Inicializa los filtros de búsqueda.
     */
    private void inicializarFiltros() {
        // Inicializar ComboBox de estados
        cmbFiltroEstado.setItems(FXCollections.observableArrayList(EstadoEvento.values()));
        cmbFiltroEstado.getItems().add(0, null); // Opción para "Todos"
        cmbFiltroEstado.setPromptText("Todos los estados");
        
        // Inicializar ComboBox de tipos
        cmbFiltroTipo.setItems(FXCollections.observableArrayList(
            "Todos", "Cine", "Taller", "Concierto", "Exposición", "Feria"
        ));
        cmbFiltroTipo.getSelectionModel().selectFirst();
        
        // Inicializar fechas
        dpFiltroFechaDesde.setValue(null);
        dpFiltroFechaHasta.setValue(null);
    }
    
    /**
     * Configura el comportamiento de selección de la tabla.
     */
    private void configurarSeleccionTabla() {
        // Habilitar/deshabilitar botones según selección
        tablaEventos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = newSelection != null;
            //btnEditarEvento.setDisable(!haySeleccion);
            btnEliminarEvento.setDisable(!haySeleccion);
            btnVerParticipantes.setDisable(!haySeleccion);
        });
        
        // Doble clic para editar
        tablaEventos.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tablaEventos.getSelectionModel().getSelectedItem() != null) {
                editarEventoSeleccionado();
            }
        });
    }
    
    /**
     * Carga todos los eventos en la tabla.
     */
    private void cargarEventos() {
        try {
            List<Evento> eventos = eventoService.buscarTodos();
            eventosObservable.clear();
            eventosObservable.addAll(eventos);
            tablaEventos.setItems(eventosObservable);
            
            // Actualizar estado de los botones
            boolean hayEventos = !eventos.isEmpty();
            tablaEventos.setPlaceholder(new Label(hayEventos ? "" : "No hay eventos registrados"));
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensajeError("Error al cargar eventos", "No se pudieron cargar los eventos: " + e.getMessage());
        }
    }
    
    /**
     * Limpia los filtros de búsqueda y muestra todos los eventos.
     */
    @FXML
    private void limpiarFiltros() {
        txtFiltroNombre.clear();
        cmbFiltroEstado.getSelectionModel().clearSelection();
        cmbFiltroEstado.setPromptText("Todos los estados");
        dpFiltroFechaDesde.setValue(null);
        dpFiltroFechaHasta.setValue(null);
        cmbFiltroTipo.getSelectionModel().selectFirst();
        
        cargarEventos();
    }
    
    /**
     * Aplica los filtros seleccionados.
     */
    @FXML
    private void aplicarFiltros() {
        try {
            // Obtener valores de filtros
            String nombre = txtFiltroNombre.getText().trim().isEmpty() ? null : txtFiltroNombre.getText().trim();
            EstadoEvento estado = cmbFiltroEstado.getValue();
            LocalDate fechaDesde = dpFiltroFechaDesde.getValue();
            LocalDate fechaHasta = dpFiltroFechaHasta.getValue();
            String tipo = cmbFiltroTipo.getValue().equals("Todos") ? null : cmbFiltroTipo.getValue();
            
            // Obtener eventos (inicialmente todos)
            List<Evento> eventos = eventoService.buscarTodos();
            
            // Filtrar por nombre si se especificó
            if (nombre != null && !nombre.isEmpty()) {
                eventos = eventos.stream()
                    .filter(e -> e.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                    .toList();
            }
            
            // Filtrar por estado si se especificó
            if (estado != null) {
                eventos = eventos.stream()
                    .filter(e -> e.getEstadoEvento() == estado)
                    .toList();
            }
            
            // Filtrar por rango de fechas si se especificaron ambas
            if (fechaDesde != null && fechaHasta != null) {
                eventos = eventos.stream()
                    .filter(e -> {
                        LocalDate fechaFin = e.getFechaInicio().plusDays(e.getDuracionEstimada());
                        return (!e.getFechaInicio().isAfter(fechaHasta) && !fechaFin.isBefore(fechaDesde));
                    })
                    .toList();
            }
            // Solo fecha desde
            else if (fechaDesde != null) {
                eventos = eventos.stream()
                    .filter(e -> {
                        LocalDate fechaFin = e.getFechaInicio().plusDays(e.getDuracionEstimada());
                        return !fechaFin.isBefore(fechaDesde);
                    })
                    .toList();
            }
            // Solo fecha hasta
            else if (fechaHasta != null) {
                eventos = eventos.stream()
                    .filter(e -> !e.getFechaInicio().isAfter(fechaHasta))
                    .toList();
            }
            
            // Filtrar por tipo si se especificó
            if (tipo != null) {
                eventos = eventos.stream()
                    .filter(e -> {
                        if (tipo.equals("Cine")) return e instanceof Cine;
                        if (tipo.equals("Taller")) return e instanceof Taller;
                        if (tipo.equals("Concierto")) return e instanceof Concierto;
                        if (tipo.equals("Exposición")) return e instanceof Exposicion;
                        if (tipo.equals("Feria")) return e instanceof Feria;
                        return true;
                    })
                    .toList();
            }
            
            // Actualizar tabla con resultados filtrados
            eventosObservable.clear();
            eventosObservable.addAll(eventos);
            
            // Mensaje si no hay resultados
            if (eventos.isEmpty()) {
                tablaEventos.setPlaceholder(new Label("No se encontraron eventos con los filtros aplicados"));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensajeError("Error al aplicar filtros", "No se pudieron aplicar los filtros: " + e.getMessage());
        }
    }

    /**
     * Abre el formulario para crear un nuevo evento.
     */
    @FXML
    private void nuevoEvento(ActionEvent event) {
        try {
            // Cargamos la vista del formulario
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/eventos/FormularioEventoView.fxml"));
            Parent root = loader.load();
            
            // Obtenemos el controlador y le pasamos referencia a este controlador
            FormularioEventoController controller = loader.getController();
            controller.setListaEventosController(this);

            Stage stage = new Stage();
            stage.setTitle("Nuevo Evento");
            Scene scene = new Scene(root);
            
            // Agrega la hoja de estilos si existe
            try {
                scene.getStylesheets().add(getClass().getResource("/css/eventos/styles-formularioevento.css").toExternalForm());
            } catch (Exception e) {
                System.out.println("CSS no encontrado: " + e.getMessage());
            }
            
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensajeError("Error al abrir el formulario", e.getMessage());
        }
    }
    
    /**
     * Abre el formulario para editar el evento seleccionado.
     */
    @FXML
    private void editarEvento(ActionEvent event) {
        editarEventoSeleccionado();
    }
    
    /**
     * Método auxiliar para editar el evento seleccionado.
     */
    private void editarEventoSeleccionado() {
        Evento eventoSeleccionado = tablaEventos.getSelectionModel().getSelectedItem();
        if (eventoSeleccionado == null) {
            mostrarMensajeError("Error", "Debe seleccionar un evento para editar");
            return;
        }
        
        try {
            // Cargamos la vista del formulario
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/eventos/FormularioEventoView.fxml"));
            Parent root = loader.load();
            
            // Obtenemos el controlador, pasamos referencias y el evento a editar
            FormularioEventoController controller = loader.getController();
            controller.setListaEventosController(this);
            controller.setEvento(eventoSeleccionado);

            Stage stage = new Stage();
            stage.setTitle("Editar Evento");
            Scene scene = new Scene(root);
            
            // Agrega la hoja de estilos si existe
            try {
                scene.getStylesheets().add(getClass().getResource("/css/eventos/styles-formularioevento.css").toExternalForm());
            } catch (Exception e) {
                System.out.println("CSS no encontrado: " + e.getMessage());
            }
            
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensajeError("Error al abrir el formulario", e.getMessage());
        }
    }
    
    /**
     * Elimina el evento seleccionado.
     */
    @FXML
    private void eliminarEvento(ActionEvent event) {
        Evento eventoSeleccionado = tablaEventos.getSelectionModel().getSelectedItem();
        if (eventoSeleccionado == null) {
            mostrarMensajeError("Error", "Debe seleccionar un evento para eliminar");
            return;
        }
        
        // Confirmar eliminación
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Está seguro que desea eliminar el evento '" + eventoSeleccionado.getNombre() + "'?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Eliminar evento
                boolean eliminado = eventoService.eliminar(eventoSeleccionado.getId());
                if (eliminado) {
                    actualizarTabla();
                    mostrarMensajeInformacion("Evento eliminado", 
                        "El evento ha sido eliminado correctamente.");
                } else {
                    mostrarMensajeError("Error", "No se pudo eliminar el evento");
                }
            } catch (Exception e) {
                e.printStackTrace();
                mostrarMensajeError("Error al eliminar", e.getMessage());
            }
        }
    }
    
    /**
     * Abre la ventana para ver los participantes del evento seleccionado.
     */
    @FXML
    private void verParticipantes() {
        Evento eventoSeleccionado = tablaEventos.getSelectionModel().getSelectedItem();
        if (eventoSeleccionado == null) {
            mostrarMensajeError("Error", "Debe seleccionar un evento para ver sus participantes");
            return;
        }

        try {
            // Cargar el FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/participaciones/ListaParticipacionesView.fxml"));
            Parent root = loader.load();
            
            // Obtener el controlador y pasar el evento seleccionado
            ListaParticipacionesController controller = loader.getController();
            controller.setEvento(eventoSeleccionado);
            
            // Configurar y mostrar la ventana
            Stage stage = new Stage();
            stage.setTitle("Participantes del evento: " + eventoSeleccionado.getNombre());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL); // Bloquea la ventana principal
            stage.initOwner(tablaEventos.getScene().getWindow()); // Establece la ventana padre
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensajeError("Error al abrir ventana", 
                "No se pudo abrir la ventana de participantes: " + e.getMessage());
        }
    }

    /**
     * Busca eventos según los filtros actuales.
     * Este método es llamado desde el botón Buscar.
     */
    @FXML
    private void buscarEventos(ActionEvent event) {
        // Simplemente delega en aplicarFiltros
        aplicarFiltros();
    }

    /**
     * Actualiza la tabla de eventos.
     * Este método es llamado desde otros controladores cuando se modifica un evento.
     */
    public void actualizarTabla() {
        cargarEventos();
    }
    
    /**
     * Muestra un mensaje de error al usuario.
     */
    private void mostrarMensajeError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Muestra un mensaje de información al usuario.
     */
    private void mostrarMensajeInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}