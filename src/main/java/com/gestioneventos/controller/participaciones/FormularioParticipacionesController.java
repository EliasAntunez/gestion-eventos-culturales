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
import com.gestioneventos.service.ParticipacionService;
import com.gestioneventos.service.PersonaService;
import com.gestioneventos.service.impl.ParticipacionServiceImpl;
import com.gestioneventos.service.impl.PersonaServiceImpl;
import com.gestioneventos.util.DateUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


/**
 * Controlador para el formulario de agregado de participantes a un evento.
 * Este formulario permite buscar personas y agregarlas como participantes
 * con el rol fijo de PARTICIPANTE.
 */
public class FormularioParticipacionesController implements Initializable {

    // Elementos de información del evento
    @FXML private Label lblTitulo;
    @FXML private Label lblNombreEvento;
    @FXML private Label lblFechaEvento;
    @FXML private Label lblTipoEvento;
    
    // Elementos de búsqueda de personas
    @FXML private TextField txtBuscarPersona;
    @FXML private Button btnBuscar;
    
    // Elementos de la tabla de personas
    @FXML private TableView<Persona> tablaPersonas;
    @FXML private TableColumn<Persona, Long> colId;
    @FXML private TableColumn<Persona, String> colNombre;
    @FXML private TableColumn<Persona, String> colApellido;
    @FXML private TableColumn<Persona, String> colDni;
    @FXML private TableColumn<Persona, String> colEmail;
    
    // Elementos de acción
    @FXML private Label lblMensaje;
    @FXML private Button btnAgregar;
    @FXML private Button btnCancelar;
    
    // Datos del controlador
    private Evento evento;
    private ObservableList<Persona> personasObservables = FXCollections.observableArrayList();
    
    // Servicios
    private final PersonaService personaService;
    private final ParticipacionService participacionService;
    
    // Controlador para actualizar después de agregar participantes
    private ListaParticipacionesController listaController;

    /**
     * Constructor que inicializa los servicios.
     */
    public FormularioParticipacionesController() {
        this.personaService = new PersonaServiceImpl();
        this.participacionService = new ParticipacionServiceImpl();
    }

    /**
     * Inicializa el controlador y configura los componentes de la interfaz.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTablaPersonas();
        cargarPersonas();
        
        // Configuración inicial de botones
        btnAgregar.setDisable(true);
        
        // Listener para habilitar/deshabilitar botón de agregar según selección
        tablaPersonas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            btnAgregar.setDisable(newVal == null);
        });
        
        // Configurar acción del botón buscar
        btnBuscar.setOnAction(event -> buscarPersonas());
        
        // Configurar acción de los botones principales
        btnAgregar.setOnAction(event -> agregarParticipante());
        btnCancelar.setOnAction(event -> cancelar());
        
        // Configurar evento Enter en campo de búsqueda
        txtBuscarPersona.setOnAction(event -> buscarPersonas());
    }

    /**
     * Configura las columnas de la tabla de personas.
     */
    private void configurarTablaPersonas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        tablaPersonas.setItems(personasObservables);
        
        // Configurar doble clic para agregar participante
        tablaPersonas.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tablaPersonas.getSelectionModel().getSelectedItem() != null) {
                agregarParticipante();
            }
        });
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
     * Carga todas las personas disponibles.
     */
    private void cargarPersonas() {
    try {
        System.out.println("Cargando personas para el evento: " + (evento != null ? evento.getId() : "null"));
        
        List<Persona> todasLasPersonas = personaService.buscarTodas();
        System.out.println("Total de personas encontradas: " + todasLasPersonas.size());
        
        // Filtrar personas que ya son participantes de este evento
        List<Participacion> participacionesExistentes = participacionService.buscarPorEvento(evento.getId());
        System.out.println("Total de participaciones existentes: " + participacionesExistentes.size());
        
        List<Long> idsPersonasParticipantes = participacionesExistentes.stream()
            .map(p -> p.getPersona().getId())
            .collect(Collectors.toList());
        
        // Mostrar solo personas que no sean ya participantes
        List<Persona> personasDisponibles = todasLasPersonas.stream()
            .filter(p -> !idsPersonasParticipantes.contains(p.getId()))
            .collect(Collectors.toList());
        
        System.out.println("Personas disponibles para agregar: " + personasDisponibles.size());
        
        personasObservables.clear();
        personasObservables.addAll(personasDisponibles);
        
        actualizarMensajeResultados(personasDisponibles.size());
    } catch (Exception e) {
            e.printStackTrace();
            mostrarErrorEnUI("Error al cargar personas: " + e.getMessage());
        }
}
    
    /**
     * Busca personas según el criterio ingresado.
     */
    @FXML
    private void buscarPersonas() {
        String criterioBusqueda = txtBuscarPersona.getText().trim();
        
        try {
            List<Persona> personasEncontradas;
            
            // Si no hay criterio de búsqueda, mostrar todas
            if (criterioBusqueda.isEmpty()) {
                personasEncontradas = personaService.buscarTodas();
            } else {
                // Buscar por nombre, apellido o DNI
                personasEncontradas = personaService.buscar(criterioBusqueda);
            }
            
            // Filtrar personas que ya son participantes
            List<Participacion> participacionesExistentes = participacionService.buscarPorEvento(evento.getId());
            List<Long> idsPersonasParticipantes = participacionesExistentes.stream()
                .map(p -> p.getPersona().getId())
                .collect(Collectors.toList());
            
            List<Persona> personasDisponibles = personasEncontradas.stream()
                .filter(p -> !idsPersonasParticipantes.contains(p.getId()))
                .collect(Collectors.toList());
            
            personasObservables.clear();
            personasObservables.addAll(personasDisponibles);
            
            actualizarMensajeResultados(personasDisponibles.size());
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErrorEnUI("Error en la búsqueda: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza el mensaje que muestra la cantidad de resultados.
     */
    private void actualizarMensajeResultados(int cantidad) {
        if (cantidad == 0) {
            lblMensaje.setText("No se encontraron personas disponibles");
        } else {
            lblMensaje.setText("");
        }
    }
    
    /**
     * Agrega la persona seleccionada como participante del evento.
     */
    @FXML
    private void agregarParticipante() {
        Persona personaSeleccionada = tablaPersonas.getSelectionModel().getSelectedItem();
        if (personaSeleccionada == null) {
            mostrarErrorEnUI("Debe seleccionar una persona para agregar como participante");
            return;
        }
        
        try {
            // Crear nueva participación (siempre con rol PARTICIPANTE)
            Participacion nuevaParticipacion = new Participacion();
            nuevaParticipacion.setEvento(evento);
            nuevaParticipacion.setPersona(personaSeleccionada);
            nuevaParticipacion.setRol(RolParticipacion.PARTICIPANTE);
            nuevaParticipacion.setFechaInscripcion(LocalDateTime.now());
            
            // Guardar la participación
            participacionService.guardar(nuevaParticipacion);
            
            // Mostrar mensaje de éxito
            mostrarMensajeExito("Participante agregado con éxito");
            
            // Actualizar la lista de participantes si existe el controlador padre
            if (listaController != null) {
                listaController.cargarParticipaciones(); // Actualizar la lista de participaciones  
            }
            
            // Eliminar la persona de la tabla
            personasObservables.remove(personaSeleccionada);
            
            // Si no quedan más personas disponibles, cerrar el formulario
            if (personasObservables.isEmpty()) {
                mostrarMensajeInformacion("No hay más personas disponibles para agregar", 
                    "Todas las personas registradas ya son participantes de este evento.");
                cerrarFormulario();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErrorEnUI("Error al agregar participante: " + e.getMessage());
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
    
    /**
     * Muestra un cuadro de diálogo con un mensaje informativo.
     */
    private void mostrarMensajeInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}