package com.gestioneventos.model.participaciones;

import com.gestioneventos.model.eventos.Evento;
import com.gestioneventos.model.personas.Persona;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa la participación de una persona en un evento con un rol específico.
 */
@Entity
@Table(name = "participaciones")
public class Participacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private RolParticipacion rol;
    
    @Column(name = "fecha_inscripcion")
    private LocalDateTime fechaInscripcion;
    
    /**
     * Constructor por defecto para JPA.
     */
    public Participacion() {
    }
    
    /**
     * Constructor con parámetros básicos, establece la fecha de inscripción al momento actual.
     * 
     * @param evento El evento en el que participa
     * @param persona La persona que participa
     * @param rol El rol de la participación
     */
    public Participacion(Evento evento, Persona persona, RolParticipacion rol) {
        this.evento = evento;
        this.persona = persona;
        this.rol = rol;
        this.fechaInscripcion = LocalDateTime.now();
    }
    
    /**
     * Constructor completo.
     * 
     * @param id ID de la participación
     * @param evento El evento en el que participa
     * @param persona La persona que participa
     * @param rol El rol de la participación
     * @param fechaInscripcion La fecha de inscripción
     */
    public Participacion(Long id, Evento evento, Persona persona, RolParticipacion rol, LocalDateTime fechaInscripcion) {
        this.id = id;
        this.evento = evento;
        this.persona = persona;
        this.rol = rol;
        this.fechaInscripcion = fechaInscripcion;
    }

    /**
     * @return El ID de la participación
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id El nuevo ID de la participación
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return El evento asociado a esta participación
     */
    public Evento getEvento() {
        return evento;
    }

    /**
     * @param evento El nuevo evento asociado a esta participación
     */
    public void setEvento(Evento evento) {
        if (evento == null) {
            throw new IllegalArgumentException("El evento no puede ser nulo");
        }
        this.evento = evento;
    }

    /**
     * @return La persona asociada a esta participación
     */
    public Persona getPersona() {
        return persona;
    }

    /**
     * @param persona La nueva persona asociada a esta participación
     */
    public void setPersona(Persona persona) {
        if (persona == null) {
            throw new IllegalArgumentException("La persona no puede ser nula");
        }
        this.persona = persona;
    }

    /**
     * @return El rol de la participación
     */
    public RolParticipacion getRol() {
        return rol;
    }

    /**
     * @param rol El nuevo rol de la participación
     */
    public void setRol(RolParticipacion rol) {
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        this.rol = rol;
    }

    /**
     * @return La fecha de inscripción
     */
    public LocalDateTime getFechaInscripcion() {
        return fechaInscripcion;
    }

    /**
     * @param fechaInscripcion La nueva fecha de inscripción
     */
    public void setFechaInscripcion(LocalDateTime fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participacion that = (Participacion) o;
        return Objects.equals(id, that.id) && 
               Objects.equals(evento.getId(), that.evento.getId()) && 
               Objects.equals(persona.getId(), that.persona.getId()) && 
               rol == that.rol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, evento.getId(), persona.getId(), rol);
    }

    @Override
    public String toString() {
        return "Participacion{" +
                "id=" + id +
                ", evento=" + (evento != null ? evento.getNombre() : "null") +
                ", persona=" + (persona != null ? persona.getNombre() : "null") +
                ", rol=" + rol +
                ", fechaInscripcion=" + fechaInscripcion +
                '}';
    }
}