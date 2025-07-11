package com.gestioneventos.model.eventos;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.gestioneventos.model.participaciones.Participacion;
import com.gestioneventos.model.participaciones.RolParticipacion;
import com.gestioneventos.model.personas.Persona;

@Entity
@Table(name = "conciertos")
public class Concierto extends Evento {
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_entrada")
    private TipoEntrada tipoEntrada;
    
    // Constructor vacío requerido por JPA
    protected Concierto() {
        super();
    }
    
    // Constructor con los atributos específicos
    public Concierto(String nombre, LocalDate fechaInicio, int duracionEstimada,
                    EstadoEvento estadoEvento, boolean permitirInscripcion,
                    TipoEntrada tipoEntrada) {
        super(nombre, fechaInicio, duracionEstimada, estadoEvento, permitirInscripcion);
        setTipoEntrada(tipoEntrada);   
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
    
    /**
     * Obtiene la lista de artistas participantes en el concierto.
     * @return Lista de personas que participan como artistas
     */
    public List<Persona> getArtistas() {
        return getParticipaciones().stream()
                .filter(p -> p.getRol() == RolParticipacion.ARTISTA)
                .map(Participacion::getPersona)
                .collect(Collectors.toList());
    }


    /**
     * Obtiene los nombres de los artistas como texto formateado
     * @return String con los nombres de los artistas separados por coma
     */
    public String getArtistasComoTexto() {
        try {
            List<Persona> artistas = getArtistas();
            if (artistas.isEmpty()) {
                return "sin artistas asignados";
            }
            
            return artistas.stream()
                .map(artista -> artista.getNombre() + " " + artista.getApellido())
                .collect(Collectors.joining(", "));
        } catch (Exception e) {
            // Si ocurre LazyInitializationException, devolver un mensaje genérico
            return "información no disponible";
        }
    }
    
    @Override
    public String obtenerDescripcionEspecifica() {
        return "Concierto de " + getArtistasComoTexto() + " - " + 
            (tipoEntrada == TipoEntrada.GRATUITA ? "Entrada gratuita" : "Entrada paga");
    }



    /**
     * Verifica si el concierto tiene artistas asignados.
     * @return true si hay al menos un artista, false en caso contrario
     */
    public boolean tieneArtistas() {
        return getParticipaciones().stream()
                .anyMatch(p -> p.getRol() == RolParticipacion.ARTISTA);
    }
    
    @Override
    public void validarRequisitosEspecificos() {
        // Validaciones específicas para conciertos
        if (!tieneArtistas()) {
            throw new IllegalStateException("El concierto debe tener al menos un artista asignado");
        }
    }
}