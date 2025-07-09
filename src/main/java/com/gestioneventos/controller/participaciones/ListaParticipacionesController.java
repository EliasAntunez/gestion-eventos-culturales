package com.gestioneventos.controller.participaciones;

import com.gestioneventos.model.eventos.Evento;
import com.gestioneventos.model.participaciones.Participacion;
import com.gestioneventos.model.participaciones.RolParticipacion;
import com.gestioneventos.model.personas.Persona;
import com.gestioneventos.service.ParticipacionService;
//import com.gestioneventos.service.PersonaService;
import com.gestioneventos.service.impl.ParticipacionServiceImpl;
//import com.gestioneventos.service.impl.PersonaServiceImpl;
import com.gestioneventos.util.DateUtils;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

//import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para la vista de listado de participaciones.
 * Permite visualizar, filtrar, crear, editar y eliminar participaciones de un evento.
 */
public class ListaParticipacionesController implements Initializable {

    // Elementos de la interfaz
    @FXML private Label lblTituloEvento;
    @FXML private TableView<Participacion> tablaParticipaciones;
    @FXML private TableColumn<Participacion, Long> colId;
    @FXML private TableColumn<Participacion, String> colNombrePersona;
    @FXML private TableColumn<Participacion, String> colApellidoPersona;
    @FXML private TableColumn<Participacion, String> colDniPersona;
    @FXML private TableColumn<Participacion, String> colRol;
    @FXML private TableColumn<Participacion, String> colFechaInscripcion;
    @FXML private Label lblCantidadParticipantes;
    
    // Elementos de filtrado
    @FXML private TextField txtFiltroNombre;
    @FXML private ComboBox<RolParticipacion> cmbFiltroRol;
    
    // Botones de acción
    @FXML private Button btnNuevoParticipante;
    @FXML private Button btnEditarParticipacion;
    @FXML private Button btnEliminarParticipacion;
    @FXML private Button btnCerrar;
    
    // Servicios
    private final ParticipacionService participacionService;
    //private final PersonaService personaService;
    
    // Datos
    private Evento evento;
    private ObservableList<Participacion> participacionesObservables;
    
    /**
     * Constructor que inicializa los servicios.
     */
    public ListaParticipacionesController() {
        this.participacionService = new ParticipacionServiceImpl();
        //this.personaService = new PersonaServiceImpl();
        this.participacionesObservables = FXCollections.observableArrayList();
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
        
        // Inicialmente, deshabilitar botones de edición y eliminación
        btnEditarParticipacion.setDisable(true);
        btnEliminarParticipacion.setDisable(true);
    }
    
    /**
     * Configura las columnas de la tabla.
     */
    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        // Nombre de persona
        colNombrePersona.setCellValueFactory(cellData -> {
            Persona persona = cellData.getValue().getPersona();
            return new SimpleStringProperty(persona != null ? persona.getNombre() : "");
        });
        
        // Apellido de persona
        colApellidoPersona.setCellValueFactory(cellData -> {
            Persona persona = cellData.getValue().getPersona();
            return new SimpleStringProperty(persona != null ? persona.getApellido() : "");
        });
        
        // DNI de persona
        colDniPersona.setCellValueFactory(cellData -> {
            Persona persona = cellData.getValue().getPersona();
            return new SimpleStringProperty(persona != null ? persona.getDni() : "");
        });
        
        // Rol participación
        colRol.setCellValueFactory(cellData -> {
            RolParticipacion rol = cellData.getValue().getRol();
            return new SimpleStringProperty(rol != null ? rol.toString() : "");
        });
        
        // Fecha inscripción
        colFechaInscripcion.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(
                DateUtils.formatLocalDateTime(cellData.getValue().getFechaInscripcion())
            );
        });
    }
    
    /**
     * Inicializa los filtros de búsqueda.
     */
    private void inicializarFiltros() {
        // Inicializar ComboBox de roles
        cmbFiltroRol.setItems(FXCollections.observableArrayList(RolParticipacion.values()));
        cmbFiltroRol.getItems().add(0, null); // Opción para "Todos"
        cmbFiltroRol.setPromptText("Todos los roles");
    }
    
    /**
     * Configura el comportamiento de selección de la tabla.
     */
    private void configurarSeleccionTabla() {
        tablaParticipaciones.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = newSelection != null;
            btnEditarParticipacion.setDisable(!haySeleccion);
            btnEliminarParticipacion.setDisable(!haySeleccion);
        });
    }
    
    /**
     * Establece el evento cuyos participantes se mostrarán.
     * @param evento El evento
     */
    public void setEvento(Evento evento) {
        this.evento = evento;
        if (evento != null) {
            lblTituloEvento.setText("Participantes del evento: " + evento.getNombre());
            cargarParticipaciones();
        }
    }
    
    /**
     * Carga todas las participaciones del evento en la tabla.
     */
    public void cargarParticipaciones() {
        if (evento == null) return;
        
        try {
            List<Participacion> participaciones = participacionService.buscarPorEvento(evento.getId());
            participacionesObservables.clear();
            participacionesObservables.addAll(participaciones);
            tablaParticipaciones.setItems(participacionesObservables);
            
            // Actualizar contador
            actualizarContadorParticipantes();
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensajeError("Error al cargar participantes", 
                "No se pudieron cargar los participantes: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza el contador de participantes.
     */
    private void actualizarContadorParticipantes() {
        int cantidad = participacionesObservables.size();
        lblCantidadParticipantes.setText("Total: " + cantidad + " participante" + (cantidad != 1 ? "s" : ""));
    }
    
    /**
     * Limpia los filtros de búsqueda y muestra todos los participantes.
     */
    @FXML
    private void limpiarFiltros() {
        txtFiltroNombre.clear();
        cmbFiltroRol.getSelectionModel().clearSelection();
        cmbFiltroRol.setPromptText("Todos los roles");
        
        cargarParticipaciones();
    }
    
    /**
     * Busca participaciones según el texto y filtros.
     */
    @FXML
    private void buscarParticipantes() {
        if (evento == null) return;
        
        try {
            String nombre = txtFiltroNombre.getText().trim();
            RolParticipacion rol = cmbFiltroRol.getValue();
            
            List<Participacion> participaciones = participacionService.buscarPorEvento(evento.getId());
            
            // Filtrar por nombre si se ingresó texto
            if (!nombre.isEmpty()) {
                participaciones = participaciones.stream()
                    .filter(p -> {
                        Persona persona = p.getPersona();
                        if (persona == null) return false;
                        
                        String nombreCompleto = persona.getNombre() + " " + persona.getApellido();
                        return nombreCompleto.toLowerCase().contains(nombre.toLowerCase());
                    })
                    .toList();
            }
            
            // Filtrar por rol si se seleccionó uno
            if (rol != null) {
                participaciones = participaciones.stream()
                    .filter(p -> p.getRol() == rol)
                    .toList();
            }
            
            // Actualizar tabla
            participacionesObservables.clear();
            participacionesObservables.addAll(participaciones);
            
            // Actualizar contador
            actualizarContadorParticipantes();
            
            // Mensaje si no hay resultados
            if (participaciones.isEmpty()) {
                tablaParticipaciones.setPlaceholder(new Label("No se encontraron participantes con los filtros aplicados"));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensajeError("Error al buscar participantes", e.getMessage());
        }
    }
    
    /**
     * Abre el formulario para agregar un nuevo participante.
     */
    @FXML
    private void nuevoParticipante() {
        if (evento == null) return;
        try {
            // Cargar el formulario de nuevo participante
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/participaciones/FormularioParticipacionesView.fxml"));
            Parent root = loader.load();
            
            FormularioParticipacionesController controller = loader.getController();
            controller.setEvento(evento);
            controller.setListaParticipacionesController(this);
            
            // Crear y mostrar la nueva ventana
            Stage stage = new Stage();
            stage.setTitle("Nuevo Participante");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensajeError("Error al abrir formulario", e.getMessage());
        }
    }
    
    /**
     * Abre el formulario para editar la participación seleccionada.
     */
    @FXML
    private void editarParticipacion() {
        Participacion participacion = tablaParticipaciones.getSelectionModel().getSelectedItem();
        if (participacion == null) {
            mostrarMensajeError("Error", "Debe seleccionar un participante para editar");
            return;
        }
        
        // En una implementación real, aquí abriríamos el formulario para editar la participación
        mostrarMensajeInformacion("Función no implementada", 
            "La funcionalidad para editar participantes aún no está implementada.");
    }
    
    /**
     * Elimina la participación seleccionada.
     */
    @FXML
    private void eliminarParticipacion() {
        Participacion participacion = tablaParticipaciones.getSelectionModel().getSelectedItem();
        if (participacion == null) {
            mostrarMensajeError("Error", "Debe seleccionar un participante para eliminar");
            return;
        }

        // Verificar si se puede eliminar según el rol y cantidad
        RolParticipacion rolParaEliminar = participacion.getRol();
        
        // Si no es un rol crítico, se puede eliminar sin verificar
        if (rolParaEliminar != RolParticipacion.ORGANIZADOR && 
            rolParaEliminar != RolParticipacion.ARTISTA && 
            rolParaEliminar != RolParticipacion.INSTRUCTOR) {
            confirmarYEliminarParticipacion(participacion);
            return;
        }
        
        // Si es un rol crítico, verificar la cantidad
        Map<RolParticipacion, Integer> cantidadPorRol = contarParticipantesPorRol();
        
        // Obtener la cantidad para el rol que se quiere eliminar
        int cantidadRol = cantidadPorRol.getOrDefault(rolParaEliminar, 0);
        
        // Verificar que haya más de uno para poder eliminar
        if (cantidadRol <= 1) {
            mostrarMensajeError("Error", 
                "No se puede eliminar el participante porque es el único con rol " + 
                rolParaEliminar.toString() + " en este evento");
            return;
        }
        
        // Si hay más de uno con ese rol, permitir eliminar
        confirmarYEliminarParticipacion(participacion);
    }

    /**
     * Cuenta la cantidad de participantes por cada rol en el evento actual.
     * @return Un mapa con la cantidad de participantes por cada rol
     */
    private Map<RolParticipacion, Integer> contarParticipantesPorRol() {
        Map<RolParticipacion, Integer> cantidadPorRol = new HashMap<>();
        
        // Inicializar el mapa con cero para cada rol
        for (RolParticipacion rol : RolParticipacion.values()) {
            cantidadPorRol.put(rol, 0);
        }
        
        // Contar los participantes por rol
        for (Participacion p : participacionesObservables) {
            RolParticipacion rol = p.getRol();
            if (rol != null) {
                cantidadPorRol.put(rol, cantidadPorRol.get(rol) + 1);
            }
        }
        
        return cantidadPorRol;
    }

    /**
     * Muestra un diálogo de confirmación y elimina la participación si el usuario confirma.
     * @param participacion La participación a eliminar
     */
    private void confirmarYEliminarParticipacion(Participacion participacion) {
        // Confirmar eliminación
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Está seguro que desea eliminar la participación de " + 
            participacion.getPersona().getNombre() + " " + participacion.getPersona().getApellido() + "?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Eliminar participación
                boolean eliminado = participacionService.eliminar(participacion.getId());
                if (eliminado) {
                    cargarParticipaciones();
                    mostrarMensajeInformacion("Participación eliminada", 
                        "La participación ha sido eliminada correctamente.");
                } else {
                    mostrarMensajeError("Error", "No se pudo eliminar la participación");
                }
            } catch (Exception e) {
                e.printStackTrace();
                mostrarMensajeError("Error al eliminar", e.getMessage());
            }
        }
    }
    
    /**
     * Cierra la ventana actual.
     */
    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
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