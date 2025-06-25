package com.gestioneventos.model.eventos;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "exposiciones")
public class Exposicion extends Evento {
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_arte", nullable = false)
    private TipoArte tipoArte;
    
    // Constructor vacío requerido por JPA
    protected Exposicion() {
        super();
    }
    
    // Constructor con los atributos específicos
    public Exposicion(String nombre, LocalDate fechaInicio, int duracionEstimada,
                    EstadoEvento estadoEvento, boolean permitirInscripcion,
                    TipoArte tipoArte) {
        super(nombre, fechaInicio, duracionEstimada, estadoEvento, permitirInscripcion);
        setTipoArte(tipoArte);
    }
    
    public TipoArte getTipoArte() {
        return tipoArte;
    }
    
    public void setTipoArte(TipoArte tipoArte) {
        if (tipoArte == null) {
            throw new IllegalArgumentException("El tipo de arte no puede ser nulo");
        }
        this.tipoArte = tipoArte;
    }
    
    @Override
    public String obtenerDescripcionEspecifica() {
        return "Exposición de " + tipoArte.toString().toLowerCase() + ": " + getNombre();
    }
    
    @Override
    public void validarRequisitosEspecificos() {
        // Una exposición requiere al menos un artista
        if (getParticipaciones().stream().noneMatch(p -> 
                p.getRol() == com.gestioneventos.model.participaciones.RolParticipacion.ARTISTA)) {
            throw new IllegalStateException("La exposición requiere al menos un artista");
        }
    }
}