package com.gestioneventos.model.eventos;

// Importaciones necesarias
import jakarta.persistence.*; // Anotaciones para mapeo objeto-relacional (ORM)
import java.time.LocalDate; // API de fechas de Java 8+
import java.util.ArrayList; // Colección para manejar relaciones
import java.util.List; // Interfaz de colección


import com.gestioneventos.model.participaciones.Participacion;
/**
 * Clase abstracta que representa un evento cultural genérico.
 * Sirve como base para todos los tipos específicos de eventos.
 * Utiliza JPA para el mapeo a la base de datos.
 */
@Entity // Marca la clase como una entidad JPA (tabla en BD)
@Table(name = "eventos")// Especifica el nombre de la tabla en la BD
@Inheritance(strategy = InheritanceType.JOINED)  // Estrategia de herencia: una tabla por clase con joins
public abstract class Evento {
    
    // Identificador único del evento, generado automáticamente
    @Id // Marca este campo como clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Autoincremento manejado por la BD
    private Long id;
    
    // Nombre descriptivo del evento
    @Column(nullable = false, length = 100) // El campo no puede ser nulo y tiene longitud máxima
    private String nombre;
    
    // Fecha en que inicia el evento
    @Column(nullable = false) // El campo no puede ser nulo
    private LocalDate fechaInicio;
    
    // Duración del evento en días
    @Column(name = "duracion_dias") // Nombre personalizado de la columna en BD
    private int duracionEstimada;
    
    // Estado actual del evento
    @Enumerated(EnumType.STRING) // Almacena el enum como string en la BD
    @Column(nullable = false) // El campo no puede ser nulo
    private EstadoEvento estadoEvento;
    
    // Indica si se permiten inscripciones al evento
    @Column(name = "permite_inscripcion") // Nombre personalizado de la columna en BD
    private boolean permiteInscripcion;
    
    // Relación con las participaciones
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Participacion> participaciones = new ArrayList<>();
    
    /**
     * Constructor vacío requerido por JPA.
     * Inicializa valores por defecto.
     */
    protected Evento() {
        // Inicializa el evento como PLANIFICADO por defecto
        this.estadoEvento = EstadoEvento.EN_PLANIFICACION;
        // Por defecto no se permiten inscripciones hasta confirmar
        this.permiteInscripcion = false;
        // Lista vacía de participaciones
        this.participaciones = new ArrayList<>();
    }
    
    /**
     * Constructor con parámetros básicos comunes a todos los eventos.
     * Utiliza los setters para aprovechar las validaciones.
     * 
     * @param nombre Nombre descriptivo del evento
     * @param fechaInicio Fecha en que inicia el evento
     * @param duracionEstimada Duración del evento en días
     */
    protected Evento(String nombre, LocalDate fechaInicio, int duracionEstimada,
                    EstadoEvento estadoEvento, boolean permiteInscripcion) {
        this();  // Llama al constructor vacío para inicializar valores por defecto
        
        // Usa los setters para aprovechar las validaciones
        setNombre(nombre);
        setFechaInicio(fechaInicio);
        setDuracionEstimada(duracionEstimada);
        setEstadoEvento(estadoEvento);
        setPermiteInscripcion(permiteInscripcion);
    }
    
    // -------------- GETTERS Y SETTERS --------------
    
    /**
     * Obtiene el ID único del evento.
     * @return ID del evento
     */
    public Long getId() {
        return id;
    }

    /**
     * Obtiene el nombre del evento.
     * @return Nombre del evento
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del evento con validaciones.
     * @param nombre Nombre a establecer
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     */
    public void setNombre(String nombre) {
        // Validación: el nombre no puede ser nulo ni vacío
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del evento no puede ser nulo o vacío");
        }
        this.nombre = nombre;
    }

    /**
     * Obtiene la fecha de inicio del evento.
     * @return Fecha de inicio
     */
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    /**
     * Establece la fecha de inicio con validaciones.
     * @param fechaInicio Fecha a establecer
     * @throws IllegalArgumentException si la fecha es nula o en el pasado
     */
    public void setFechaInicio(LocalDate fechaInicio) {
        // Validación: la fecha no puede ser nula
        if (fechaInicio == null) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser nula");
        }
        // Validación: la fecha no puede ser en el pasado
        if (fechaInicio.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser anterior a la fecha actual");
        }
        this.fechaInicio = fechaInicio;
    }

    /**
     * Obtiene la duración estimada en días.
     * @return Duración en días
     */
    public int getDuracionEstimada() {
        return duracionEstimada;
    }

    /**
     * Establece la duración estimada con validaciones.
     * @param duracionEstimada Días de duración
     * @throws IllegalArgumentException si la duración no es positiva
     */
    public void setDuracionEstimada(int duracionEstimada) {
        // Validación: la duración debe ser positiva
        if (duracionEstimada <= 0) {
            throw new IllegalArgumentException("La duración estimada debe ser un valor positivo");
        }
        this.duracionEstimada = duracionEstimada;
    }

    /**
     * Obtiene el estado actual del evento.
     * @return Estado del evento
     */
    public EstadoEvento getEstadoEvento() {
        return estadoEvento;
    }

    /*set de EstadoEvento */
    public void setEstadoEvento(EstadoEvento estadoEvento) {
        this.estadoEvento = estadoEvento;
    }

    /**
     * Verifica si el evento permite inscripciones.
     * @return true si permite inscripciones, false en caso contrario
     */
    public boolean getPermiteInscripcion() {
        return permiteInscripcion;
    }
    /*set de permitir inscripcion */
    public void setPermiteInscripcion(boolean permitirInscripcion) {
        this.permiteInscripcion = permitirInscripcion;
    }

    /**
     * Obtiene las participaciones asociadas a este evento.
     * @return Lista de participaciones
     */
    public List<Participacion> getParticipaciones() {
        return new ArrayList<>(participaciones); // Devuelve copia para evitar modificaciones externas
    }

    // -------------- MÉTODOS DE NEGOCIO --------------
    
    /**
     * Cambia el estado del evento y actualiza las reglas de negocio asociadas.
     * Implementa validaciones para transiciones de estado permitidas.
     * 
     * @param nuevoEstado El nuevo estado para el evento
     * @throws IllegalArgumentException si el nuevo estado es nulo
     * @throws IllegalStateException si la transición de estado no está permitida
     */
    public void cambiarEstado(EstadoEvento nuevoEstado) {
        // Validación: el nuevo estado no puede ser nulo
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado no puede ser nulo");
        }
        
        // Validación: no se puede cambiar de estado un evento CANCELADO
        if (this.estadoEvento == EstadoEvento.CANCELADO) {
            throw new IllegalStateException("Un evento cancelado no puede cambiar de estado");
        }
        
        // Validación: no se puede cambiar de estado un evento FINALIZADO
        if (this.estadoEvento == EstadoEvento.FINALIZADO) {
            throw new IllegalStateException("Un evento finalizado no puede cambiar de estado");
        }
        
        // Validación: no puede regresar de CONFIRMADO a PLANIFICADO
        if (this.estadoEvento == EstadoEvento.CONFIRMADO && nuevoEstado == EstadoEvento.EN_PLANIFICACION) {
            throw new IllegalStateException("Un evento confirmado no puede volver a estado planificado");
        }
        
        // Establece el nuevo estado
        this.estadoEvento = nuevoEstado;
        
        // Actualiza las reglas de negocio asociadas al estado
        actualizarPermitirInscripcion();
    }
    
    /**
     * Actualiza la bandera de permitir inscripción basada en el estado actual.
     * Solo se permiten inscripciones para eventos CONFIRMADOS.
     */
    private void actualizarPermitirInscripcion() {
        this.permiteInscripcion = (this.estadoEvento == EstadoEvento.CONFIRMADO);
    }
    
    
    /**
     * Añade una participación al evento.
     * @param participacion Participación a añadir
     * @throws IllegalArgumentException si la participación es nula
     * @throws IllegalStateException si no se permiten inscripciones o se alcanzó el límite
     */
    public void agregarParticipacion(Participacion participacion) {
        // Validación: la participación no puede ser nula
        if (participacion == null) {
            throw new IllegalArgumentException("La participación no puede ser nula");
        }
        
        // Validación: solo se permiten inscripciones si la bandera está activada
        if (!permiteInscripcion) {
            throw new IllegalStateException("No se permiten inscripciones para este evento");
        }
        
        // Asociación bidireccional
        participacion.setEvento(this);
        participaciones.add(participacion);
    }
    
    // -------------- MÉTODOS ABSTRACTOS --------------
    
    /**
     * Método abstracto para obtener la descripción específica del tipo de evento.
     * Cada subclase debe implementar este método según sus características.
     * 
     * @return String con detalles específicos del tipo de evento
     */
    public abstract String obtenerDescripcionEspecifica();
    
    
    /**
     * Método abstracto para validar requisitos específicos del tipo de evento.
     * Cada subclase debe implementar este método según sus restricciones.
     * 
     * @throws IllegalStateException si no cumple los requisitos específicos
     */
    public abstract void validarRequisitosEspecificos();
    
    // -------------- MÉTODOS OVERRIDE --------------
    
    /**
     * Representación en texto del evento.
     * @return Cadena con información básica del evento
     */
    @Override
    public String toString() {
        return "Evento: " + nombre + 
               " (Inicia: " + fechaInicio + 
               ", Duración: " + duracionEstimada + " días" +
               ", Estado: " + estadoEvento + ")";
    }
    
    /**
     * Compara si este evento es igual a otro objeto.
     * Dos eventos son iguales si tienen el mismo ID o el mismo nombre y fecha.
     * 
     * @param o Objeto a comparar
     * @return true si son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Evento)) return false;
        
        Evento evento = (Evento) o;
        
        // Si ambos tienen ID, comparar por ID
        if (id != null && evento.id != null) {
            return id.equals(evento.id);
        }
        
        // Si alguno no tiene ID, comparar por nombre y fecha
        return nombre.equals(evento.nombre) && 
               fechaInicio.equals(evento.fechaInicio);
    }
    
    /**
     * Genera un código hash para el evento.
     * @return Código hash basado en ID o nombre y fecha
     */
    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        int result = nombre.hashCode();
        result = 31 * result + fechaInicio.hashCode();
        return result;
    }
}