package com.gestioneventos.controller.participaciones;

import com.gestioneventos.model.eventos.Evento;
import com.gestioneventos.model.eventos.Cine;
import com.gestioneventos.model.eventos.Taller;
import com.gestioneventos.model.eventos.Concierto;
import com.gestioneventos.model.eventos.Exposicion;
import com.gestioneventos.model.eventos.Feria;
import com.gestioneventos.model.participaciones.Participacion;
import com.gestioneventos.model.participaciones.RolParticipacion;
import com.gestioneventos.model.personas.Persona;
import com.gestioneventos.service.ServicioParticipacion;
import com.gestioneventos.service.ServicioPersona;
import com.gestioneventos.util.DateUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controlador para el formulario de agregado de participantes a un evento.
 * Este formulario permite seleccionar personas y agregarlas como participantes
 * con el rol fijo de PARTICIPANTE.
 */
public class FormularioParticipacionesController implements Initializable {

    // Elementos de información del evento
    @FXML private Label lblTitulo;
    @FXML private Label lblNombreEvento;
    @FXML private Label lblFechaEvento;
    @FXML private Label lblTipoEvento;
    
    // Elementos para la gestión de participantes
    @FXML private ComboBox<Persona> cmbPersonas;
    @FXML private Button btnAgregarParticipante;
    @FXML private TableView<Persona> tablaParticipantesSeleccionados;
    @FXML private TableColumn<Persona, String> colNombre;
    @FXML private TableColumn<Persona, String> colApellido;
    @FXML private TableColumn<Persona, String> colDni;
    @FXML private TableColumn<Persona, Void> colAccion;
    @FXML private Label lblParticipantesInfo;
    
    // Elementos de acción
    @FXML private Label lblMensaje;
    @FXML private Button btnAgregar;
    @FXML private Button btnCancelar;
    
    // Datos del controlador
    private Evento evento;
    private ObservableList<Persona> participantesSeleccionados = FXCollections.observableArrayList();
    
    // Servicios
    private final ServicioPersona personaService;
    private final ServicioParticipacion participacionService;
    
    // Controlador para actualizar después de agregar participantes
    private ListaParticipacionesController listaController;

    /**
     * Constructor que inicializa los servicios.
     */
    public FormularioParticipacionesController() {
        this.personaService = new ServicioPersona();
        this.participacionService = new ServicioParticipacion();
    }

    /**
     * Inicializa el controlador y configura los componentes de la interfaz.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar la tabla de participantes seleccionados
        configurarTablaParticipantes();
        
        // Configuración inicial de botones
        btnAgregar.setDisable(true);
        
        // Listener para habilitar/deshabilitar botón de agregar según selección
        participantesSeleccionados.addListener((javafx.collections.ListChangeListener.Change<? extends Persona> c) -> {
            btnAgregar.setDisable(participantesSeleccionados.isEmpty());
            actualizarMensajeInfoParticipantes();
        });
        
        // Configurar acciones de los botones
        btnAgregarParticipante.setOnAction(event -> agregarParticipanteSeleccionado());
        btnAgregar.setOnAction(event -> agregarParticipantes());
        btnCancelar.setOnAction(event -> cancelar());
        
        // Establecer estilo para el label de información
        lblParticipantesInfo.getStyleClass().add("nota-obligatorio");
    }

    /**
     * Configura la tabla de participantes seleccionados.
     */
    private void configurarTablaParticipantes() {
        // Configurar columnas
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        
        // Configurar la columna de acción (botón eliminar)
        configurarColumnaAccion();
        
        // Inicializar lista observable
        tablaParticipantesSeleccionados.setItems(participantesSeleccionados);
    }

    /**
     * Configura la columna de acción con botones para eliminar participantes.
     */
    private void configurarColumnaAccion() {
        Callback<TableColumn<Persona, Void>, TableCell<Persona, Void>> cellFactory = 
            new Callback<TableColumn<Persona, Void>, TableCell<Persona, Void>>() {
                @Override
                public TableCell<Persona, Void> call(final TableColumn<Persona, Void> param) {
                    final TableCell<Persona, Void> cell = new TableCell<Persona, Void>() {
                        private final Button btn = new Button("Eliminar");
                        {
                            btn.setOnAction(event -> {
                                Persona persona = getTableView().getItems().get(getIndex());
                                participantesSeleccionados.remove(persona);
                                actualizarComboBoxPersonas();
                            });
                            btn.getStyleClass().add("btn-danger");
                            btn.setMaxWidth(Double.MAX_VALUE);
                        }

                        @Override
                        protected void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(btn);
                            }
                        }
                    };
                    return cell;
                }
            };
        colAccion.setCellFactory(cellFactory);
    }

    /**
     * Establece el evento al que se agregarán participantes y actualiza la interfaz.
     * @param evento El evento seleccionado
     */
    public void setEvento(Evento evento) {
        this.evento = evento;
        if (evento != null) {
            actualizarDatosEvento();
            cargarPersonas(); // Carga inicial de personas
        }
    }
    
    /**
     * Actualiza la información mostrada del evento.
     */
    private void actualizarDatosEvento() {
        lblNombreEvento.setText(evento.getNombre());
        lblFechaEvento.setText(DateUtils.formatLocalDate(evento.getFechaInicio()));
        
        // Determinar el tipo de evento
        String tipoEvento = "Evento";
        if (evento instanceof Cine) tipoEvento = "Cine";
        else if (evento instanceof Taller) tipoEvento = "Taller";
        else if (evento instanceof Concierto) tipoEvento = "Concierto";
        else if (evento instanceof Exposicion) tipoEvento = "Exposición";
        else if (evento instanceof Feria) tipoEvento = "Feria";
        
        lblTipoEvento.setText(tipoEvento);
    }
    
    /**
     * Establece una referencia al controlador de la lista para actualizarla después de agregar.
     * @param controller El controlador de la lista de participaciones
     */
    public void setListaParticipacionesController(ListaParticipacionesController controller) {
        this.listaController = controller;
    }
    
    /**
     * Carga todas las personas disponibles para el ComboBox.
     */
    private void cargarPersonas() {
        try {
            // Obtener todas las personas
            List<Persona> todasLasPersonas = personaService.buscarTodas();
            
            // Filtrar personas que ya son participantes de este evento
            List<Participacion> participacionesExistentes = participacionService.buscarPorEvento(evento.getId());
            List<Long> idsPersonasParticipantes = participacionesExistentes.stream()
                .map(p -> p.getPersona().getId())
                .collect(Collectors.toList());
            
            // Mostrar solo personas que no sean ya participantes
            List<Persona> personasDisponibles = todasLasPersonas.stream()
                .filter(p -> !idsPersonasParticipantes.contains(p.getId()))
                .collect(Collectors.toList());
            
            actualizarComboBoxConPersonas(personasDisponibles);
            
            // Actualizar mensaje informativo
            actualizarMensajeInfoParticipantes();
            
            // Mostrar mensaje si no hay personas disponibles
            if (personasDisponibles.isEmpty()) {
                lblMensaje.setText("No hay personas disponibles para agregar como participantes");
            } else {
                lblMensaje.setText("");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErrorEnUI("Error al cargar personas: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza el ComboBox de personas con la lista proporcionada y configura su formato.
     */
    private void actualizarComboBoxConPersonas(List<Persona> personas) {
        // Cargar en el ComboBox
        cmbPersonas.setItems(FXCollections.observableArrayList(personas));
        
        // Configurar cómo se muestran las personas en el ComboBox
        cmbPersonas.setCellFactory(lv -> new ListCell<Persona>() {
            @Override
            protected void updateItem(Persona persona, boolean empty) {
                super.updateItem(persona, empty);
                setText(empty ? "" : persona.getNombre() + " " + persona.getApellido() + " - " + persona.getDni());
            }
        });
        
        cmbPersonas.setButtonCell(new ListCell<Persona>() {
            @Override
            protected void updateItem(Persona persona, boolean empty) {
                super.updateItem(persona, empty);
                setText(empty ? "" : persona.getNombre() + " " + persona.getApellido() + " - " + persona.getDni());
            }
        });
    }
    
    /**
     * Actualiza el ComboBox eliminando las personas ya seleccionadas.
     */
    private void actualizarComboBoxPersonas() {
        try {
            // Obtener todas las personas
            List<Persona> todasLasPersonas = personaService.buscarTodas();
            
            // Filtrar personas que ya son participantes de este evento
            List<Participacion> participacionesExistentes = participacionService.buscarPorEvento(evento.getId());
            List<Long> idsPersonasParticipantes = participacionesExistentes.stream()
                .map(p -> p.getPersona().getId())
                .collect(Collectors.toList());
            
            // Filtrar también las personas ya seleccionadas
            List<Long> idsPersonasSeleccionadas = participantesSeleccionados.stream()
                .map(Persona::getId)
                .collect(Collectors.toList());
            
            // Mostrar solo personas disponibles
            List<Persona> personasDisponibles = todasLasPersonas.stream()
                .filter(p -> !idsPersonasParticipantes.contains(p.getId()) && !idsPersonasSeleccionadas.contains(p.getId()))
                .collect(Collectors.toList());
            
            actualizarComboBoxConPersonas(personasDisponibles);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErrorEnUI("Error al actualizar lista de personas: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza el mensaje informativo sobre participantes seleccionados.
     */
    private void actualizarMensajeInfoParticipantes() {
        int cantidadParticipantes = participantesSeleccionados.size();
        if (cantidadParticipantes == 0) {
            lblParticipantesInfo.setText("Debe seleccionar al menos un participante");
            lblParticipantesInfo.getStyleClass().add("error-label");
        } else {
            lblParticipantesInfo.setText("Participantes seleccionados: " + cantidadParticipantes);
            lblParticipantesInfo.getStyleClass().remove("error-label");
        }
    }
    
    /**
     * Agrega una persona seleccionada del ComboBox a la lista de participantes.
     */
    @FXML
    private void agregarParticipanteSeleccionado() {
        Persona personaSeleccionada = cmbPersonas.getValue();
        
        if (personaSeleccionada == null) {
            mostrarErrorEnUI("Debe seleccionar una persona para agregarla como participante");
            return;
        }
        
        // Verificar si ya está agregada
        if (participantesSeleccionados.contains(personaSeleccionada)) {
            mostrarErrorEnUI("Esta persona ya está agregada como participante");
            return;
        }
        
        // Agregar a la lista y limpiar mensaje de error
        participantesSeleccionados.add(personaSeleccionada);
        lblMensaje.setText("");
        
        // Limpiar selección
        cmbPersonas.setValue(null);
        
        // Actualizar combo para eliminar la persona ya seleccionada
        actualizarComboBoxPersonas();
    }
    
    /**
     * Agrega todos los participantes seleccionados al evento y cierra el formulario.
     */
    @FXML
    private void agregarParticipantes() {
        if (participantesSeleccionados.isEmpty()) {
            mostrarErrorEnUI("Debe seleccionar al menos una persona para agregar como participante");
            return;
        }
        
        try {
            int participantesAgregados = 0;
            
            for (Persona persona : participantesSeleccionados) {
                // Crear nueva participación con rol PARTICIPANTE
                Participacion nuevaParticipacion = new Participacion();
                nuevaParticipacion.setEvento(evento);
                nuevaParticipacion.setPersona(persona);
                nuevaParticipacion.setRol(RolParticipacion.PARTICIPANTE);
                nuevaParticipacion.setFechaInscripcion(LocalDateTime.now());
                
                // Guardar la participación
                participacionService.guardar(nuevaParticipacion);
                participantesAgregados++;
            }
            
            // Mostrar mensaje de éxito
            mostrarMensajeExito(participantesAgregados + " participante(s) agregado(s) con éxito");
            
            // Actualizar la lista de participantes si existe el controlador padre
            if (listaController != null) {
                listaController.cargarParticipaciones();
            }
            
            // Cerrar el formulario
            cerrarFormulario();
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErrorEnUI("Error al guardar participantes: " + e.getMessage());
        }
    }
    
    /**
     * Cancela la operación y cierra el formulario.
     */
    @FXML
    private void cancelar() {
        cerrarFormulario();
    }
    
    /**
     * Cierra la ventana del formulario.
     */
    private void cerrarFormulario() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Muestra un mensaje de error en la etiqueta de mensaje.
     */
    private void mostrarErrorEnUI(String mensaje) {
        lblMensaje.setText(mensaje);
    }
    
    /**
     * Muestra un cuadro de diálogo con un mensaje de éxito.
     */
    private void mostrarMensajeExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Operación exitosa");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}