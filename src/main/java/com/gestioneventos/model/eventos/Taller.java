package com.gestioneventos.model.eventos;

import jakarta.persistence.*;
import java.time.LocalDate;

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
    
    @Override
    public String obtenerDescripcionEspecifica() {
        return "Taller " + getNombre() + " - Modalidad: " + modalidad + 
               ", Cupo máximo: " + cupoMaximo + " participantes";
    }
    
    @Override
    public void validarRequisitosEspecificos() {
        // Un taller requiere al menos un instructor
        if (getParticipaciones().stream().noneMatch(p -> 
                p.getRol() == com.gestioneventos.model.participaciones.RolParticipacion.INSTRUCTOR)) {
            throw new IllegalStateException("El taller requiere al menos un instructor");
        }
    }
}