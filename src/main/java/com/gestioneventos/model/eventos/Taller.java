package com.gestioneventos.model.eventos;

import jakarta.persistence.*;
import java.time.LocalDate;

import com.gestioneventos.model.participaciones.Participacion;
import com.gestioneventos.model.participaciones.RolParticipacion;
import com.gestioneventos.model.personas.Persona;

@Entity
@Table(name = "talleres")
public class Taller extends Evento {
    
    @Column(name = "cupo_maximo")
    private int cupoMaximo;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Modalidad modalidad;
    
    // Constructor vacío requerido por JPA
    protected Taller() {
        super();
    }
    
    // Constructor con los atributos específicos de Taller
    public Taller(String nombre, LocalDate fechaInicio, int duracionEstimada,
                 EstadoEvento estadoEvento, boolean permitirInscripcion,
                 int cupoMaximo, Modalidad modalidad) {
        super(nombre, fechaInicio, duracionEstimada, estadoEvento, permitirInscripcion);
        setCupoMaximo(cupoMaximo);
        setModalidad(modalidad);
    }
    
    public int getCupoMaximo() {
        return cupoMaximo;
    }
    
    public void setCupoMaximo(int cupoMaximo) {
        if (cupoMaximo <= 0) {
            throw new IllegalArgumentException("El cupo máximo debe ser un valor positivo");
        }
        this.cupoMaximo = cupoMaximo;
    }
    
    public Modalidad getModalidad() {
        return modalidad;
    }
    
    public void setModalidad(Modalidad modalidad) {
        if (modalidad == null) {
            throw new IllegalArgumentException("La modalidad no puede ser nula");
        }
        this.modalidad = modalidad;
    }

    /**
     * Obtiene el instructor del taller.
     * @return La persona asignada como instructor, o null si no hay instructor asignado
     */
    public Persona getInstructor() {
        return getParticipaciones().stream()
                .filter(p -> p.getRol() == RolParticipacion.INSTRUCTOR)
                .map(Participacion::getPersona)
                .findFirst()
                .orElse(null);
    }

    /**
     * Verifica si el taller tiene un instructor asignado.
     * @return true si hay un instructor, false en caso contrario
     */
    public boolean tieneInstructor() {
        return getParticipaciones().stream()
                .anyMatch(p -> p.getRol() == RolParticipacion.INSTRUCTOR);
    }
    
    @Override
    public String obtenerDescripcionEspecifica() {
        return "Taller " + getNombre() + " - Modalidad: " + modalidad + 
               ", Cupo máximo: " + cupoMaximo + " participantes";
    }
    
    @Override
    public void validarRequisitosEspecificos() {
        // Un taller requiere al menos un instructor
        if (!tieneInstructor()) {
            throw new IllegalStateException("El taller debe tener al menos un instructor asignado");
        }
    }
}