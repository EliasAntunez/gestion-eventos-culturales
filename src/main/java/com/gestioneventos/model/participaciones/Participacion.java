package com.gestioneventos.model.participaciones;

import jakarta.persistence.*;
import com.gestioneventos.model.eventos.Evento;
import com.gestioneventos.model.personas.Persona;

/**
 * Clase que representa la relación entre una Persona y un Evento.
 * Permite modelar diferentes roles de participación como asistente, organizador, etc.
 */
@Entity
@Table(name = "participaciones")
public class Participacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;
    
    @ManyToOne
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolParticipacion rol;
    
    @Column(name = "fecha_inscripcion")
    private java.time.LocalDate fechaInscripcion;
    
    @Column(length = 500)
    private String observaciones;
    
    // Constructor vacío requerido por JPA
    protected Participacion() {
        this.fechaInscripcion = java.time.LocalDate.now();
    }
    
    // Constructor con parámetros básicos
    public Participacion(Persona persona, Evento evento, RolParticipacion rol) {
        this();
        this.persona = persona;
        this.evento = evento;
        this.rol = rol;
    }
    
    // Getters y setters
    
    public Long getId() {
        return id;
    }
    
    public Persona getPersona() {
        return persona;
    }
    
    public void setPersona(Persona persona) {
        if (persona == null) {
            throw new IllegalArgumentException("La persona no puede ser nula");
        }
        this.persona = persona;
    }
    
    public Evento getEvento() {
        return evento;
    }
    
    public void setEvento(Evento evento) {
        if (evento == null) {
            throw new IllegalArgumentException("El evento no puede ser nulo");
        }
        this.evento = evento;
    }
    
    public RolParticipacion getRol() {
        return rol;
    }
    
    public void setRol(RolParticipacion rol) {
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        this.rol = rol;
    }
    
    public java.time.LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    @Override
    public String toString() {
        return "Participación: " + persona.getNombre() + " " + persona.getApellido() + 
               " como " + rol + " en " + evento.getNombre();
    }
}