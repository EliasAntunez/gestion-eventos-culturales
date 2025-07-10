// c:\2025 POO I - Trabajo Integrador\gestion-eventos-culturales\src\main\java\com\gestioneventos\controller\personas\FormularioPersonaController.java
package com.gestioneventos.controller.personas;

import com.gestioneventos.model.personas.Persona;
import com.gestioneventos.service.ServicioPersona;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para el formulario de creación y edición de personas.
 * Maneja tanto la creación de nuevas personas como la edición de existentes.
 */
public class FormularioPersonaController implements Initializable {

    // Elementos de la interfaz definidos en el FXML
    @FXML
    private Label lblTitulo; // Etiqueta del título (cambia según sea creación o edición)
    
    @FXML
    private TextField txtNombre; // Campo para el nombre
    
    @FXML
    private TextField txtApellido; // Campo para el apellido
    
    @FXML
    private TextField txtDni; // Campo para el DNI
    
    @FXML
    private TextField txtTelefono; // Campo para el teléfono
    
    @FXML
    private TextField txtEmail; // Campo para el email
    
    @FXML
    private Button btnGuardar; // Botón para guardar cambios
    
    @FXML
    private Button btnCancelar; // Botón para cancelar
    
    @FXML
    private Label lblError; // Etiqueta para mostrar mensajes de error
    
    // Variables de estado
    private Persona persona; // Persona que se está editando (null si es nueva)
    private final ServicioPersona personaService; // Servicio para operaciones con personas
    private ListaPersonasController listaPersonasController; // Referencia al controlador de la lista
    private boolean esEdicion = false; // Indica si estamos en modo edición o creación
    
    /**
     * Constructor que inicializa el servicio.
     */
    public FormularioPersonaController() {
        this.personaService = new ServicioPersona();
    }
    
    /**
     * Método llamado automáticamente al inicializar la vista.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Limpiar mensaje de error al inicio
        lblError.setText("");
    }
    
    /**
     * Establece la persona a editar y cambia el modo a edición.
     * @param persona Persona a editar
     */
    public void setPersona(Persona persona) {
        this.persona = persona;
        this.esEdicion = true;
        
        // Cargamos los datos de la persona en el formulario
        cargarDatosPersona();
        // Cambiamos el título para indicar edición
        lblTitulo.setText("Editar Persona");
    }
    
    /**
     * Establece la referencia al controlador de la lista para actualizar al guardar.
     * @param controller Controlador de la lista de personas
     */
    public void setListaPersonasController(ListaPersonasController controller) {
        this.listaPersonasController = controller;
    }
    
    /**
     * Carga los datos de la persona en los campos del formulario.
     */
    private void cargarDatosPersona() {
        if (persona != null) {
            // Rellenamos cada campo con los datos de la persona
            txtNombre.setText(persona.getNombre());
            txtApellido.setText(persona.getApellido());
            txtDni.setText(persona.getDni());
            txtTelefono.setText(persona.getTelefono());
            txtEmail.setText(persona.getEmail());
        }
    }
    
    /**
     * Guarda los cambios realizados en el formulario.
     * Crea una nueva persona o actualiza una existente según el modo.
     */
    @FXML
    private void guardar(ActionEvent event) {
        try {
            // Validar los datos antes de guardar
            if (validarCampos()) {
                if (!esEdicion) {
                    // Si es una nueva persona, la creamos con los datos del formulario
                    persona = new Persona(
                            txtNombre.getText().trim().toUpperCase(),
                            txtApellido.getText().trim().toUpperCase(),
                            txtDni.getText().trim().toUpperCase(),
                            txtTelefono.getText().trim().toUpperCase(),
                            txtEmail.getText().trim()
                    );
                } else {
                    // Si es edición, actualizamos los datos de la persona existente
                    persona.setNombre(txtNombre.getText().trim().toUpperCase());
                    persona.setApellido(txtApellido.getText().trim().toUpperCase());
                    persona.setDni(txtDni.getText().trim().toUpperCase());
                    persona.setTelefono(txtTelefono.getText().trim().toUpperCase());
                    persona.setEmail(txtEmail.getText().trim());
                }
                
                // Guardamos la persona usando el servicio
                Persona personaGuardada = personaService.guardar(persona);
                
                // Actualizamos la tabla en el controlador de lista
                if (listaPersonasController != null) {
                    listaPersonasController.actualizarTabla();
                }
                
                // Mostrar mensaje de éxito
                mostrarMensajeExito(personaGuardada);
                
                // Cerramos la ventana
                cerrarVentana();
            }
        } catch (IllegalArgumentException e) {
            // Capturamos excepciones de validación
            lblError.setText(e.getMessage());
            // Mostrar alerta de error
            mostrarMensajeError("Error de validación", e.getMessage());
        } catch (Exception e) {
            // Capturamos otras excepciones
            lblError.setText("Error al guardar: " + e.getMessage());
            // Mostrar alerta de error
            mostrarMensajeError("Error al guardar", e.getMessage());
        }
    }
    
        /**
     * Muestra un mensaje de éxito cuando la persona se guarda correctamente.
     * @param personaGuardada La persona que se guardó correctamente
     */
    private void mostrarMensajeExito(Persona personaGuardada) {
        String operacion = esEdicion ? "actualizada" : "creada";
        String titulo = esEdicion ? "Actualización exitosa" : "Registro exitoso";
        
        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText("La persona " + personaGuardada.getNombre() + " " + personaGuardada.getApellido() + " ha sido " + operacion + " correctamente.");
        alerta.showAndWait();
    }
    
    /**
     * Muestra un mensaje de error cuando ocurre un problema al guardar.
     * 
     * @param titulo Título de la alerta
     * @param mensaje Mensaje detallado del error
     */
    private void mostrarMensajeError(String titulo, String mensaje) {
        Alert alerta = new Alert(AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    
    /**
     * Cancela la operación y cierra la ventana.
     */
    @FXML
    private void cancelar(ActionEvent event) {
        cerrarVentana();
    }
    
    /**
     * Valida que los campos obligatorios estén rellenos correctamente.
     * @return true si los campos son válidos, false en caso contrario
     */
    private boolean validarCampos() {
    // Validar nombre
    String nombre = txtNombre.getText();
    if (nombre == null || nombre.trim().isEmpty()) {
        lblError.setText("El nombre es obligatorio");
        return false;
    }
    if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
        lblError.setText("El nombre solo puede contener letras");
        return false;
    }

    // Validar apellido
    String apellido = txtApellido.getText();
    if (apellido == null || apellido.trim().isEmpty()) {
        lblError.setText("El apellido es obligatorio");
        return false;
    }
    if (!apellido.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
        lblError.setText("El apellido solo puede contener letras");
        return false;
    }

    // Validar DNI
    String dni = txtDni.getText();
    if (dni == null || dni.trim().isEmpty()) {
        lblError.setText("El DNI es obligatorio");
        return false;
    }
    if (!dni.matches("\\d+")) {
        lblError.setText("El DNI solo puede contener números");
        return false;
    }
    if (dni.length() < 7 || dni.length() > 8) {
        lblError.setText("El DNI debe tener 7 u 8 dígitos");
        return false;
    }

    // Validar email
    String email = txtEmail.getText();
    if (email == null || email.trim().isEmpty()) {
        lblError.setText("El Email es obligatorio");
        return false;
    }
    if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
    lblError.setText("Ingrese un Email válido");
    return false;
    }

    // Validar DNI duplicado
    if (!esEdicion || !dni.equals(persona.getDni())) {
        Long idExcluido = esEdicion ? persona.getId() : null;
        if (personaService.existeDniDuplicado(dni, idExcluido)) {
            lblError.setText("Ya existe una persona con ese DNI");
            return false;
        }
    }

    // Si pasó todas las validaciones
    return true;
    }
    
    /**
     * Cierra la ventana actual.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}