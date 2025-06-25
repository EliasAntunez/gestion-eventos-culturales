package com.gestioneventos.model.eventos;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "conciertos")
public class Concierto extends Evento {
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_entrada")
    private TipoEntrada tipoEntrada;
    
    @Column(name = "artista_principal")
    private String artistaPrincipal;
    
    // Constructor vacío requerido por JPA
    protected Concierto() {
        super();
    }
    
    // Constructor con los atributos específicos
    public Concierto(String nombre, LocalDate fechaInicio, int duracionEstimada,
                    EstadoEvento estadoEvento, boolean permitirInscripcion,
                    TipoEntrada tipoEntrada, String artistaPrincipal) {
        super(nombre, fechaInicio, duracionEstimada, estadoEvento, permitirInscripcion);
        setTipoEntrada(tipoEntrada);   
        setArtistaPrincipal(artistaPrincipal);
    }
    
    public TipoEntrada getTipoEntrada() {
        return tipoEntrada;
    }
    
    public void setTipoEntrada(TipoEntrada tipoEntrada) {
        if (tipoEntrada == null) {
            throw new IllegalArgumentException("El tipo de entrada no puede ser nulo");
        }
        this.tipoEntrada = tipoEntrada;
    }
    
    public String getArtistaPrincipal() {
        return artistaPrincipal;
    }
    
    public void setArtistaPrincipal(String artistaPrincipal) {
        if (artistaPrincipal == null || artistaPrincipal.trim().isEmpty()) {
            throw new IllegalArgumentException("El artista principal no puede ser nulo o vacío");
        }
        this.artistaPrincipal = artistaPrincipal;
    }
    
    @Override
    public String obtenerDescripcionEspecifica() {
        return "Concierto de " + artistaPrincipal + " - " + 
               (tipoEntrada == TipoEntrada.GRATUITA ? "Entrada gratuita" : "Entrada paga");
    }
    
    @Override
    public void validarRequisitosEspecificos() {
        // Validaciones específicas para conciertos
        if (artistaPrincipal == null || artistaPrincipal.trim().isEmpty()) {
            throw new IllegalStateException("El concierto debe tener un artista principal definido");
        }
    }
}