// c:\2025 POO I - Trabajo Integrador\gestion-eventos-culturales\src\main\java\com\gestioneventos\controller\personas\ListaPersonasController.java
package com.gestioneventos.controller.personas;

import com.gestioneventos.model.personas.Persona;
import com.gestioneventos.service.PersonaService;
import com.gestioneventos.service.impl.PersonaServiceImpl;

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
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controlador para la vista de lista de personas.
 * Maneja la visualización, búsqueda, edición y eliminación de personas.
 */
public class ListaPersonasController implements Initializable {

    // Elementos de la interfaz definidos en el FXML
    @FXML
    private TableView<Persona> tablaPersonas; // Tabla principal de personas
    
    @FXML
    private TableColumn<Persona, String> colId; // Columna para ID
    
    @FXML
    private TableColumn<Persona, String> colNombre; // Columna para nombre
    
    @FXML
    private TableColumn<Persona, String> colApellido; // Columna para apellido
    
    @FXML
    private TableColumn<Persona, String> colDni; // Columna para DNI
    
    @FXML
    private TableColumn<Persona, String> colTelefono; // Columna para teléfono
    
    @FXML
    private TableColumn<Persona, String> colEmail; // Columna para email
    
    @FXML
    private TextField txtBuscar; // Campo de búsqueda
    
    @FXML
    private Button btnBuscar; // Botón para ejecutar búsqueda
    
    @FXML
    private Button btnNuevo; // Botón para crear nueva persona
    
    @FXML
    private Button btnEditar; // Botón para editar persona seleccionada
    
    @FXML
    private Button btnEliminar; // Botón para eliminar persona seleccionada
    
    @FXML
    private Button btnVolver; // Botón para volver a la pantalla anterior
    
    // Servicio para operaciones de negocio con personas
    private final PersonaService personaService;
    
    // Lista observable para mostrar en la tabla
    private ObservableList<Persona> listaPersonas;
    
    /**
     * Constructor que inicializa el servicio y la lista observable.
     */
    public ListaPersonasController() {
        // Inicializamos el servicio
        this.personaService = new PersonaServiceImpl();
        // Inicializamos la lista observable vacía
        this.listaPersonas = FXCollections.observableArrayList();
    }
    
    /**
     * Método llamado automáticamente al inicializar la vista.
     * Configura la tabla y carga los datos iniciales.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configuramos las columnas de la tabla
        configurarColumnas();
        
        // Cargamos los datos iniciales
        cargarPersonas();
        
        // Configuramos la selección de la tabla para habilitar/deshabilitar botones
        tablaPersonas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            // Habilitamos botones Editar/Eliminar solo cuando hay selección
            btnEditar.setDisable(newSelection == null);
            btnEliminar.setDisable(newSelection == null);
        });
        
        // Configuración de doble clic para editar
        tablaPersonas.setOnMouseClicked(this::manejarDobleClicTabla);
        
        // Estado inicial de botones: deshabilitados hasta que se seleccione
        btnEditar.setDisable(true);
        btnEliminar.setDisable(true);
    }
    
    /**
     * Configura las columnas de la tabla para mostrar los datos de Persona.
     * Utiliza cell factories para convertir propiedades a cadenas.
     */
    private void configurarColumnas() {
        // Columna ID: convertimos Long a String para mostrar
        colId.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getId() != null ? 
                cellData.getValue().getId().toString() : ""));
        
        // Columna Nombre
        colNombre.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNombre()));
        
        // Columna Apellido
        colApellido.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getApellido()));
        
        // Columna DNI
        colDni.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDni()));
        
        // Columna Teléfono
        colTelefono.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTelefono()));
        
        // Columna Email
        colEmail.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEmail()));
    }
    
    /**
     * Carga todas las personas desde el servicio y las muestra en la tabla.
     */
    private void cargarPersonas() {
        // Limpiamos la lista actual
        listaPersonas.clear();
        // Obtenemos todas las personas del servicio
        List<Persona> personas = personaService.buscarTodas();
        // Añadimos las personas a la lista observable
        listaPersonas.addAll(personas);
        // Establecemos la lista observable como fuente de datos de la tabla
        tablaPersonas.setItems(listaPersonas);
    }
    
    /**
     * Maneja el evento del botón de búsqueda.
     * Busca personas según el texto ingresado.
     */
    @FXML
    private void buscarPersonas(ActionEvent event) {
        // Obtenemos el texto de búsqueda
        String texto = txtBuscar.getText().trim();
        // Limpiamos la lista actual
        listaPersonas.clear();
        // Buscamos personas que coincidan con el texto
        List<Persona> personas = personaService.buscar(texto);
        // Añadimos los resultados a la lista observable
        listaPersonas.addAll(personas);
    }
    
    /**
     * Abre el formulario para crear una nueva persona.
     */
    @FXML
    private void nuevaPersona(ActionEvent event) {
        try {
            // Cargamos la vista del formulario
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/personas/FormularioPersonaView.fxml"));
            Parent root = loader.load();
            
            // Obtenemos el controlador y le pasamos referencia a este controlador
            FormularioPersonaController controller = loader.getController();
            controller.setListaPersonasController(this);
            
            Stage stage = new Stage();
            stage.setTitle("Nueva Persona");
            Scene scene = new Scene(root);
            // Agrega la hoja de estilos
            //scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensajeError("Error al abrir el formulario", e.getMessage());
        }
    }
    
    /**
     * Abre el formulario para editar la persona seleccionada.
     */
    @FXML
    private void editarPersona(ActionEvent event) {
        abrirFormularioEdicion();
    }
    
    /**
     * Maneja el evento de doble clic en la tabla.
     * Abre el formulario de edición para la fila seleccionada.
     */
    private void manejarDobleClicTabla(MouseEvent event) {
        if (event.getClickCount() == 2) {
            // Si es doble clic, abrimos formulario de edición
            abrirFormularioEdicion();
        }
    }
    
    /**
     * Método auxiliar para abrir el formulario de edición.
     * Verifica que haya una persona seleccionada y carga el formulario.
     */
    private void abrirFormularioEdicion() {
        // Obtener la persona seleccionada
        Persona personaSeleccionada = tablaPersonas.getSelectionModel().getSelectedItem();
        if (personaSeleccionada == null) {
            return; // No hay selección, no hacemos nada
        }
        
        try {
            // Cargamos la vista del formulario
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/personas/FormularioPersonaView.fxml"));
            Parent root = loader.load();
            
            // Obtenemos el controlador y le pasamos los datos necesarios
            FormularioPersonaController controller = loader.getController();
            controller.setPersona(personaSeleccionada); // Pasamos la persona a editar
            controller.setListaPersonasController(this); // Pasamos referencia a este controlador
            
            // Creamos y configuramos la ventana
            Stage stage = new Stage();
            stage.setTitle("Editar Persona");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch (IOException e) {
            mostrarMensajeError("Error al abrir el formulario", e.getMessage());
        }
    }
    
    /**
     * Elimina la persona seleccionada tras pedir confirmación.
     */
    @FXML
    private void eliminarPersona(ActionEvent event) {
        // Obtener la persona seleccionada
        Persona personaSeleccionada = tablaPersonas.getSelectionModel().getSelectedItem();
        if (personaSeleccionada == null) {
            return; // No hay selección, no hacemos nada
        }
        
        // Mostrar diálogo de confirmación
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Está seguro de que desea eliminar a " + 
                             personaSeleccionada.getNombre() + " " + 
                             personaSeleccionada.getApellido() + "?");
        
        // Esperar respuesta del usuario
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Eliminar la persona usando el servicio
                boolean eliminado = personaService.eliminar(personaSeleccionada.getId());
                if (eliminado) {
                    cargarPersonas(); // Recargamos la tabla
                    mostrarMensajeInfo("Persona eliminada", "La persona ha sido eliminada correctamente.");
                } else {
                    mostrarMensajeError("Error", "No se pudo eliminar la persona.");
                }
            } catch (Exception e) {
                mostrarMensajeError("Error al eliminar", e.getMessage());
            }
        }
    }
    
    /**
     * Cierra la ventana actual para volver a la anterior.
     */
    @FXML
    private void volver(ActionEvent event) {
        // Obtener la ventana actual y cerrarla
        ((Stage) btnVolver.getScene().getWindow()).close();
    }
    
    /**
     * Método público para actualizar la tabla desde otros controladores.
     * Usado desde FormularioPersonaController al guardar cambios.
     */
    public void actualizarTabla() {
        cargarPersonas();
    }
    
    /**
     * Muestra un mensaje de información al usuario.
     */
    private void mostrarMensajeInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
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
}