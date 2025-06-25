package com.gestioneventos.controller.eventos;

import com.gestioneventos.model.eventos.*;
import com.gestioneventos.model.participaciones.Participacion;
import com.gestioneventos.model.participaciones.RolParticipacion;
import com.gestioneventos.service.EventoService;
import com.gestioneventos.service.impl.EventoServiceImpl;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.gestioneventos.service.PersonaService;
import com.gestioneventos.service.impl.PersonaServiceImpl;

import com.gestioneventos.model.personas.Persona;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
//import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para el formulario de creación y edición de eventos.
 * Maneja tanto la creación de nuevos eventos como la edición de existentes
 * de diferentes tipos (Cine, Taller, Concierto, Exposición, Feria).
 */
public class FormularioEventoController implements Initializable {

    // Referencia al controlador de la lista de eventos
    private ListaEventosController listaEventosController;

    // Elementos FXML - Generales
    @FXML private Label lblTitulo;
    @FXML private Label lblError;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    
    // Campos de datos generales
    @FXML private ComboBox<String> cmbTipoEvento;
    @FXML private TextField txtNombre;
    @FXML private DatePicker dpFechaInicio;
    @FXML private TextField txtDuracionEstimada;
    @FXML private ComboBox<EstadoEvento> cmbEstadoEvento;
    @FXML private CheckBox chkPermiteInscripcion;
    
    // Contenedor de tipos específicos
    @FXML private VBox contenedorTiposEvento;
    
    // Paneles específicos por tipo
    @FXML private GridPane gridCine;
    @FXML private GridPane gridTaller;
    @FXML private GridPane gridConcierto;
    @FXML private GridPane gridExposicion;
    @FXML private GridPane gridFeria;
    
    // Campos específicos - Cine
    @FXML private TextField txtOrdenProyeccion;
    @FXML private TextField txtTituloPelicula;
    
    // Campos específicos - Taller
    @FXML private TextField txtCupoMaximo;
    @FXML private ComboBox<Modalidad> cmbModalidad;
    
    // Campos específicos - Concierto
    @FXML private TextField txtArtistaPrincipal;
    @FXML private ComboBox<TipoEntrada> cmbTipoEntrada;
    
    // Campos específicos - Exposición
    @FXML private ComboBox<TipoArte> cmbTipoArte;
    
    // Campos específicos - Feria
    @FXML private TextField txtCantidadStands;
    @FXML private ComboBox<TipoUbicacion> cmbTipoUbicacion;

    // Añadir estos atributos a la clase
    @FXML private ComboBox<Persona> cmbPersonas;
    @FXML private Button btnAgregarOrganizador;
    @FXML private TableView<Persona> tablaOrganizadores;
    @FXML private TableColumn<Persona, String> colNombre;
    @FXML private TableColumn<Persona, String> colApellido;
    @FXML private TableColumn<Persona, String> colDni;
    @FXML private TableColumn<Persona, Void> colAccion;
    @FXML private Label lblOrganizadoresInfo;

    
    // Variables de estado
    private Evento eventoEditando;
    private final EventoService eventoService;
    private boolean esEdicion = false;

    // Añadir estas declaraciones al inicio de la clase, junto con las otras variables
    private final PersonaService personaService;
    private ObservableList<Persona> organizadoresSeleccionados;
    
    /**
     * Constructor que inicializa el servicio.
     */
    public FormularioEventoController() {
        this.eventoService = new EventoServiceImpl();
        this.personaService = new PersonaServiceImpl();
    }
    
    /**
     * Método que se llama al inicializar la vista.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Limpiar mensaje de error
        lblError.setText("");
        
        // Inicializar ComboBox de tipo de evento
        cmbTipoEvento.setItems(FXCollections.observableArrayList(
                "Cine", "Taller", "Concierto", "Exposición", "Feria"));
                
        // Listener para cambiar visibilidad de formularios específicos
        cmbTipoEvento.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    mostrarFormularioEspecifico(newValue);
                }
            }
        );
        
        // Inicializar ComboBox de estado evento
        cmbEstadoEvento.setItems(FXCollections.observableArrayList(EstadoEvento.values()));
        cmbEstadoEvento.getSelectionModel().selectFirst();
        
        // Inicializar ComboBox específicos
        cmbModalidad.setItems(FXCollections.observableArrayList(Modalidad.values()));
        cmbTipoEntrada.setItems(FXCollections.observableArrayList(TipoEntrada.values()));
        cmbTipoArte.setItems(FXCollections.observableArrayList(TipoArte.values()));
        cmbTipoUbicacion.setItems(FXCollections.observableArrayList(TipoUbicacion.values()));
        
        // Configurar fechas para no permitir fechas anteriores a hoy
        dpFechaInicio.setValue(LocalDate.now());
        
        // Por defecto, ocultar todos los formularios específicos
        ocultarTodosLosFormulariosEspecificos();

        // Configurar la tabla de organizadores
        configurarTablaOrganizadores();

        // Cargar lista de personas para el ComboBox
        cargarPersonas();
        
        // Establecer estilo para el label de información
        lblOrganizadoresInfo.getStyleClass().add("nota-obligatorio");
    }

    /**
     * Configura la tabla de organizadores seleccionados.
     */
    private void configurarTablaOrganizadores() {
        // Configurar columnas
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        
        // Configurar la columna de acción (botón eliminar)
        configurarColumnaAccion();
        
        // Inicializar lista observable
        organizadoresSeleccionados = FXCollections.observableArrayList();
        tablaOrganizadores.setItems(organizadoresSeleccionados);
    }

    /**
     * Configura la columna de acción con botones para eliminar organizadores.
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
                                organizadoresSeleccionados.remove(persona);
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
     * Carga la lista de personas para seleccionar organizadores.
     */
    private void cargarPersonas() {
        try {
            List<Persona> personas = personaService.buscarTodas();
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
            
        } catch (Exception e) {
            mostrarError("Error al cargar lista de personas: " + e.getMessage());
        }
    }

    /**
     * Agrega un organizador a la lista de organizadores seleccionados.
     * @param event El evento de acción
     */
    @FXML
    private void agregarOrganizador(ActionEvent event) {
        Persona personaSeleccionada = cmbPersonas.getValue();
        
        if (personaSeleccionada == null) {
            mostrarError("Debe seleccionar una persona para agregarla como organizador");
            return;
        }
        
        // Verificar si ya está agregada
        if (organizadoresSeleccionados.contains(personaSeleccionada)) {
            mostrarError("Esta persona ya está agregada como organizador");
            return;
        }
        
        // Agregar a la lista y limpiar mensaje de error
        organizadoresSeleccionados.add(personaSeleccionada);
        lblError.setText("");
        
        // Limpiar selección
        cmbPersonas.setValue(null);
    }

    
    /**
     * Establece el evento a editar y configura el formulario en modo edición.
     * @param evento El evento a editar
     */
    public void setEvento(Evento evento) {
        this.eventoEditando = evento;
        this.esEdicion = true;
        
        // Cargar datos del evento
        cargarDatosEvento();
        
        // Cambiar título
        lblTitulo.setText("Editar Evento");
    }
    
    /**
     * Configura la referencia al controlador de la lista de eventos.
     * @param controller El controlador de la lista de eventos
     */
    public void setListaEventosController(ListaEventosController controller) {
        this.listaEventosController = controller;
    }
    
    /**
     * Carga los datos del evento en el formulario.
     */
    private void cargarDatosEvento() {
        if (eventoEditando != null) {
            // Cargar datos generales
            txtNombre.setText(eventoEditando.getNombre());
            dpFechaInicio.setValue(eventoEditando.getFechaInicio());
            txtDuracionEstimada.setText(String.valueOf(eventoEditando.getDuracionEstimada()));
            cmbEstadoEvento.setValue(eventoEditando.getEstadoEvento());
            chkPermiteInscripcion.setSelected(eventoEditando.getPermiteInscripcion());
            
            // Determinar y seleccionar el tipo de evento
            String tipoEvento = determinarTipoEvento(eventoEditando);
            cmbTipoEvento.setValue(tipoEvento);
            
            // Cargar datos específicos según tipo
            switch (tipoEvento) {
                case "Cine" -> cargarDatosCine((Cine) eventoEditando);
                case "Taller" -> cargarDatosTaller((Taller) eventoEditando);
                case "Concierto" -> cargarDatosConcierto((Concierto) eventoEditando);
                case "Exposición" -> cargarDatosExposicion((Exposicion) eventoEditando);
                case "Feria" -> cargarDatosFeria((Feria) eventoEditando);
            }
            
            // Deshabilitar cambio de tipo para eventos existentes
            cmbTipoEvento.setDisable(true);
            
            // Cargar organizadores
            try {
                List<Participacion> participaciones = eventoService.obtenerParticipaciones(eventoEditando.getId());
                for (Participacion participacion : participaciones) {
                    if (participacion.getRol() == RolParticipacion.ORGANIZADOR) {
                        organizadoresSeleccionados.add(participacion.getPersona());
                    }
                }
            } catch (Exception e) {
                mostrarError("Error al cargar organizadores: " + e.getMessage());
            }
        }
    }
    
    /**
     * Determina el tipo de evento basado en la instancia.
     * @param evento El evento a analizar
     * @return String representando el tipo de evento
     */
    private String determinarTipoEvento(Evento evento) {
        if (evento instanceof Cine) return "Cine";
        if (evento instanceof Taller) return "Taller";
        if (evento instanceof Concierto) return "Concierto";
        if (evento instanceof Exposicion) return "Exposición";
        if (evento instanceof Feria) return "Feria";
        return null;
    }
    
    /**
     * Carga los datos específicos de un evento de tipo Cine.
     * @param cine Evento de cine a cargar
     */
    private void cargarDatosCine(Cine cine) {
        txtOrdenProyeccion.setText(String.valueOf(cine.getOrdenProyeccion()));
        txtTituloPelicula.setText(cine.getTituloPelicula());
    }
    
    /**
     * Carga los datos específicos de un evento de tipo Taller.
     * @param taller Evento de taller a cargar
     */
    private void cargarDatosTaller(Taller taller) {
        txtCupoMaximo.setText(String.valueOf(taller.getCupoMaximo()));
        cmbModalidad.setValue(taller.getModalidad());
    }
    
    /**
     * Carga los datos específicos de un evento de tipo Concierto.
     * @param concierto Evento de concierto a cargar
     */
    private void cargarDatosConcierto(Concierto concierto) {
        txtArtistaPrincipal.setText(concierto.getArtistaPrincipal());
        cmbTipoEntrada.setValue(concierto.getTipoEntrada());
    }
    
    /**
     * Carga los datos específicos de un evento de tipo Exposición.
     * @param exposicion Evento de exposición a cargar
     */
    private void cargarDatosExposicion(Exposicion exposicion) {
        cmbTipoArte.setValue(exposicion.getTipoArte());
    }
    
    /**
     * Carga los datos específicos de un evento de tipo Feria.
     * @param feria Evento de feria a cargar
     */
    private void cargarDatosFeria(Feria feria) {
        txtCantidadStands.setText(String.valueOf(feria.getCantidadStands()));
        cmbTipoUbicacion.setValue(feria.getTipoUbicacion());
    }
    
    /**
     * Muestra el formulario específico según el tipo de evento seleccionado.
     * @param tipoEvento Tipo de evento seleccionado
     */
    private void mostrarFormularioEspecifico(String tipoEvento) {
        // Primero ocultar TODOS los formularios y restablecer posiciones
        ocultarTodosLosFormulariosEspecificos();
        
        // Mostrar SOLO el seleccionado en la misma posición
        switch (tipoEvento) {
            case "Cine" -> gridCine.setVisible(true);
            case "Taller" -> gridTaller.setVisible(true);
            case "Concierto" -> gridConcierto.setVisible(true);
            case "Exposición" -> gridExposicion.setVisible(true);
            case "Feria" -> gridFeria.setVisible(true);
        }
    }
    
    /**
     * Oculta todos los formularios específicos.
     */
    private void ocultarTodosLosFormulariosEspecificos() {
        gridCine.setVisible(false);
        gridTaller.setVisible(false);
        gridConcierto.setVisible(false);
        gridExposicion.setVisible(false);
        gridFeria.setVisible(false);
    }
    
    /**
     * Guarda el evento actual, ya sea creando uno nuevo o actualizando uno existente.
     * @param event El evento de acción
     */
    @FXML
    private void guardar(ActionEvent event) {
        try {
            // Validar campos generales
            if (!validarCamposGenerales()) {
                return;
            }
            
            // Obtener datos generales
            String nombre = txtNombre.getText().trim();
            LocalDate fechaInicio = dpFechaInicio.getValue();
            int duracionEstimada = Integer.parseInt(txtDuracionEstimada.getText().trim());
            EstadoEvento estadoEvento = cmbEstadoEvento.getValue();
            boolean permiteInscripcion = chkPermiteInscripcion.isSelected();
            
            // Crear evento según tipo seleccionado
            Evento eventoGuardar = null;
            String tipoSeleccionado = cmbTipoEvento.getValue();
            
            if (tipoSeleccionado != null) {
                switch (tipoSeleccionado) {
                    case "Cine" -> eventoGuardar = guardarCine(nombre, fechaInicio, duracionEstimada, estadoEvento, permiteInscripcion);
                    case "Taller" -> eventoGuardar = guardarTaller(nombre, fechaInicio, duracionEstimada, estadoEvento, permiteInscripcion);
                    case "Concierto" -> eventoGuardar = guardarConcierto(nombre, fechaInicio, duracionEstimada, estadoEvento, permiteInscripcion);
                    case "Exposición" -> eventoGuardar = guardarExposicion(nombre, fechaInicio, duracionEstimada, estadoEvento, permiteInscripcion);
                    case "Feria" -> eventoGuardar = guardarFeria(nombre, fechaInicio, duracionEstimada, estadoEvento, permiteInscripcion);
                }
                
                // Si el proceso de guardar específico devolvió null, detener el proceso
                if (eventoGuardar == null) {
                    return;
                }
                
                // Guardar el evento usando el servicio
                Evento eventoGuardado = eventoService.guardar(eventoGuardar);
                
                // Luego de guardar el evento, añadir:
                if (eventoGuardado != null) {
                    // Agregar organizadores
                    for (Persona organizador : organizadoresSeleccionados) {
                        try {
                            eventoService.agregarParticipacion(eventoGuardado.getId(), organizador, RolParticipacion.ORGANIZADOR);
                        } catch (IllegalArgumentException e) {
                            // Si ya existe la participación, ignorar el error
                            if (!e.getMessage().contains("ya está inscrita")) {
                                throw e;
                            }
                        }
                    }

                    // Actualizar lista en el controlador principal
                    if (listaEventosController != null) {
                        listaEventosController.actualizarTabla();
                    }
                    
                    // Mostrar mensaje de éxito
                    mostrarMensajeExito(eventoGuardado);
                    
                    // Cerrar ventana
                    cerrarVentana();
                } else {
                    mostrarError("No se pudo guardar el evento");
                }
            } else {
                mostrarError("Debe seleccionar un tipo de evento");
            }
            
        } catch (NumberFormatException e) {
            lblError.setText("Error en formato numérico: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            lblError.setText(e.getMessage());
        } catch (Exception e) {
            lblError.setText("Error al guardar: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Crea o actualiza un evento de tipo Cine.
     */
    private Evento guardarCine(String nombre, LocalDate fechaInicio, int duracionEstimada, 
                             EstadoEvento estadoEvento, boolean permiteInscripcion) {
        if (!validarCamposCine()) {
            return null;
        }
        
        int ordenProyeccion = Integer.parseInt(txtOrdenProyeccion.getText().trim());
        String tituloPelicula = txtTituloPelicula.getText().trim();
        
        if (esEdicion && eventoEditando instanceof Cine) {
            // Actualizar existente
            Cine cine = (Cine) eventoEditando;
            cine.setNombre(nombre);
            cine.setFechaInicio(fechaInicio);
            cine.setDuracionEstimada(duracionEstimada);
            cine.setEstadoEvento(estadoEvento);
            cine.setPermiteInscripcion(permiteInscripcion);
            cine.setOrdenProyeccion(ordenProyeccion);
            cine.setTituloPelicula(tituloPelicula);
            return cine;
        } else {
            // Crear nuevo
            return new Cine(nombre, fechaInicio, duracionEstimada, estadoEvento, permiteInscripcion, 
                           ordenProyeccion, tituloPelicula);
        }
    }
    
    /**
     * Crea o actualiza un evento de tipo Taller.
     */
    private Evento guardarTaller(String nombre, LocalDate fechaInicio, int duracionEstimada, 
                               EstadoEvento estadoEvento, boolean permiteInscripcion) {
        if (!validarCamposTaller()) {
            return null;
        }
        
        int cupoMaximo = Integer.parseInt(txtCupoMaximo.getText().trim());
        Modalidad modalidad = cmbModalidad.getValue();
        
        if (esEdicion && eventoEditando instanceof Taller) {
            // Actualizar existente
            Taller taller = (Taller) eventoEditando;
            taller.setNombre(nombre);
            taller.setFechaInicio(fechaInicio);
            taller.setDuracionEstimada(duracionEstimada);
            taller.setEstadoEvento(estadoEvento);
            taller.setPermiteInscripcion(permiteInscripcion);
            taller.setCupoMaximo(cupoMaximo);
            taller.setModalidad(modalidad);
            return taller;
        } else {
            // Crear nuevo
            return new Taller(nombre, fechaInicio, duracionEstimada, estadoEvento, permiteInscripcion, 
                             cupoMaximo, modalidad);
        }
    }
    
    /**
     * Crea o actualiza un evento de tipo Concierto.
     */
    private Evento guardarConcierto(String nombre, LocalDate fechaInicio, int duracionEstimada, 
                                  EstadoEvento estadoEvento, boolean permiteInscripcion) {
        if (!validarCamposConcierto()) {
            return null;
        }
        
        String artistaPrincipal = txtArtistaPrincipal.getText().trim();
        TipoEntrada tipoEntrada = cmbTipoEntrada.getValue();
        
        if (esEdicion && eventoEditando instanceof Concierto) {
            // Actualizar existente
            Concierto concierto = (Concierto) eventoEditando;
            concierto.setNombre(nombre);
            concierto.setFechaInicio(fechaInicio);
            concierto.setDuracionEstimada(duracionEstimada);
            concierto.setEstadoEvento(estadoEvento);
            concierto.setPermiteInscripcion(permiteInscripcion);
            concierto.setArtistaPrincipal(artistaPrincipal);
            concierto.setTipoEntrada(tipoEntrada);
            return concierto;
        } else {
            // Crear nuevo
            return new Concierto(nombre, fechaInicio, duracionEstimada, estadoEvento, permiteInscripcion, 
                                tipoEntrada, artistaPrincipal);
        }
    }
    
    /**
     * Crea o actualiza un evento de tipo Exposición.
     */
    private Evento guardarExposicion(String nombre, LocalDate fechaInicio, int duracionEstimada, 
                                   EstadoEvento estadoEvento, boolean permiteInscripcion) {
        if (!validarCamposExposicion()) {
            return null;
        }
        
        TipoArte tipoArte = cmbTipoArte.getValue();
        
        if (esEdicion && eventoEditando instanceof Exposicion) {
            // Actualizar existente
            Exposicion exposicion = (Exposicion) eventoEditando;
            exposicion.setNombre(nombre);
            exposicion.setFechaInicio(fechaInicio);
            exposicion.setDuracionEstimada(duracionEstimada);
            exposicion.setEstadoEvento(estadoEvento);
            exposicion.setPermiteInscripcion(permiteInscripcion);
            exposicion.setTipoArte(tipoArte);
            return exposicion;
        } else {
            // Crear nuevo
            return new Exposicion(nombre, fechaInicio, duracionEstimada, estadoEvento, permiteInscripcion, 
                                 tipoArte);
        }
    }
    
    /**
     * Crea o actualiza un evento de tipo Feria.
     */
    private Evento guardarFeria(String nombre, LocalDate fechaInicio, int duracionEstimada, 
                              EstadoEvento estadoEvento, boolean permiteInscripcion) {
        if (!validarCamposFeria()) {
            return null;
        }
        
        int cantidadStands = Integer.parseInt(txtCantidadStands.getText().trim());
        TipoUbicacion tipoUbicacion = cmbTipoUbicacion.getValue();
        
        if (esEdicion && eventoEditando instanceof Feria) {
            // Actualizar existente
            Feria feria = (Feria) eventoEditando;
            feria.setNombre(nombre);
            feria.setFechaInicio(fechaInicio);
            feria.setDuracionEstimada(duracionEstimada);
            feria.setEstadoEvento(estadoEvento);
            feria.setPermiteInscripcion(permiteInscripcion);
            feria.setCantidadStands(cantidadStands);
            feria.setTipoUbicacion(tipoUbicacion);
            return feria;
        } else {
            // Crear nuevo
            return new Feria(nombre, fechaInicio, duracionEstimada, estadoEvento, permiteInscripcion, 
                            cantidadStands, tipoUbicacion);
        }
    }
    
    /**
     * Valida los campos generales del evento.
     * @return true si los campos son válidos, false en caso contrario
     */
    private boolean validarCamposGenerales() {
        // Validar tipo de evento
        if (cmbTipoEvento.getValue() == null) {
            mostrarError("Debe seleccionar un tipo de evento");
            return false;
        }
        
        // Validar nombre
        if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) {
            mostrarError("El nombre es obligatorio");
            return false;
        }
        
        // Validar fecha de inicio
        if (dpFechaInicio.getValue() == null) {
            mostrarError("La fecha de inicio es obligatoria");
            return false;
        }
        
        // Validar duración
        try {
            int duracion = Integer.parseInt(txtDuracionEstimada.getText().trim());
            if (duracion <= 0) {
                mostrarError("La duración debe ser un número positivo");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarError("La duración debe ser un número entero válido");
            return false;
        }
        
        // Validar estado
        if (cmbEstadoEvento.getValue() == null) {
            mostrarError("Debe seleccionar un estado para el evento");
            return false;
        }

        // Validar que haya al menos un organizador
        if (organizadoresSeleccionados.isEmpty()) {
            mostrarError("Debe seleccionar al menos un organizador");
            return false;
        }
        
        return true;
    }
    
    /**
     * Valida los campos específicos de un evento tipo Cine.
     * @return true si los campos son válidos, false en caso contrario
     */
    private boolean validarCamposCine() {
        // Validar orden de proyección
        try {
            int orden = Integer.parseInt(txtOrdenProyeccion.getText().trim());
            if (orden <= 0) {
                mostrarError("El orden de proyección debe ser un número positivo");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarError("El orden de proyección debe ser un número entero válido");
            return false;
        }
        
        // Validar título de película
        if (txtTituloPelicula.getText() == null || txtTituloPelicula.getText().trim().isEmpty()) {
            mostrarError("El título de la película es obligatorio");
            return false;
        }
        
        return true;
    }
    
    /**
     * Valida los campos específicos de un evento tipo Taller.
     * @return true si los campos son válidos, false en caso contrario
     */
    private boolean validarCamposTaller() {
        // Validar cupo máximo
        try {
            int cupo = Integer.parseInt(txtCupoMaximo.getText().trim());
            if (cupo <= 0) {
                mostrarError("El cupo máximo debe ser un número positivo");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarError("El cupo máximo debe ser un número entero válido");
            return false;
        }
        
        // Validar modalidad
        if (cmbModalidad.getValue() == null) {
            mostrarError("Debe seleccionar una modalidad");
            return false;
        }
        
        return true;
    }
    
    /**
     * Valida los campos específicos de un evento tipo Concierto.
     * @return true si los campos son válidos, false en caso contrario
     */
    private boolean validarCamposConcierto() {
        // Validar artista principal
        if (txtArtistaPrincipal.getText() == null || txtArtistaPrincipal.getText().trim().isEmpty()) {
            mostrarError("El artista principal es obligatorio");
            return false;
        }
        
        // Validar tipo de entrada
        if (cmbTipoEntrada.getValue() == null) {
            mostrarError("Debe seleccionar un tipo de entrada");
            return false;
        }
        
        return true;
    }
    
    /**
     * Valida los campos específicos de un evento tipo Exposición.
     * @return true si los campos son válidos, false en caso contrario
     */
    private boolean validarCamposExposicion() {
        // Validar tipo de arte
        if (cmbTipoArte.getValue() == null) {
            mostrarError("Debe seleccionar un tipo de arte");
            return false;
        }
        
        return true;
    }
    
    /**
     * Valida los campos específicos de un evento tipo Feria.
     * @return true si los campos son válidos, false en caso contrario
     */
    private boolean validarCamposFeria() {
        // Validar cantidad de stands
        try {
            int stands = Integer.parseInt(txtCantidadStands.getText().trim());
            if (stands <= 0) {
                mostrarError("La cantidad de stands debe ser un número positivo");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarError("La cantidad de stands debe ser un número entero válido");
            return false;
        }
        
        // Validar tipo de ubicación
        if (cmbTipoUbicacion.getValue() == null) {
            mostrarError("Debe seleccionar un tipo de ubicación");
            return false;
        }
        
        return true;
    }
    
    /**
     * Muestra un mensaje de error en la etiqueta de error.
     * @param mensaje El mensaje de error a mostrar
     */
    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
    }
    
    /**
     * Muestra un mensaje de éxito cuando se guarda el evento.
     * @param evento El evento guardado
     */
    private void mostrarMensajeExito(Evento evento) {
        String operacion = esEdicion ? "actualizado" : "creado";
        String tipoEvento = determinarTipoEvento(evento);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Operación exitosa");
        alert.setHeaderText(null);
        alert.setContentText("El evento de tipo " + tipoEvento + " ha sido " + operacion + " correctamente.");
        alert.showAndWait();
    }
    
    /**
     * Cancela la operación actual y cierra la ventana.
     * @param event El evento de acción
     */
    @FXML
    private void cancelar(ActionEvent event) {
        cerrarVentana();
    }
    
    /**
     * Cierra la ventana del formulario.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    /**
     * Obtiene el controlador de la lista de eventos.
     * @return El controlador de la lista de eventos
     */
    public ListaEventosController getListaEventosController() {
        return listaEventosController;
    }

    
    @FXML
    private void cambiarTipoEvento(ActionEvent event) {
        // Tu lógica para cambiar la visibilidad de los formularios específicos
        String tipoSeleccionado = cmbTipoEvento.getValue();
        
        // Ocultar todos los formularios específicos
        gridCine.setVisible(false);
        gridTaller.setVisible(false);
        gridConcierto.setVisible(false);
        gridExposicion.setVisible(false);
        gridFeria.setVisible(false);
        
        // Mostrar el formulario correspondiente al tipo seleccionado
        switch (tipoSeleccionado) {
            case "Cine":
                gridCine.setVisible(true);
                break;
            case "Taller":
                gridTaller.setVisible(true);
                break;
            case "Concierto":
                gridConcierto.setVisible(true);
                break;
            case "Exposición":
                gridExposicion.setVisible(true);
                break;
            case "Feria":
                gridFeria.setVisible(true);
                break;
            default:
                // No mostrar ningún formulario específico
                break;
        }
    }
    

}