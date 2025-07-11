package com.gestioneventos.service;

import com.gestioneventos.model.participaciones.Participacion;
import com.gestioneventos.repositorio.RepositorioParticipacion;

import java.util.List;

/**
 * Servicio para la gestión de participaciones en eventos.
 */
public class ServicioParticipacion {

    private final RepositorioParticipacion repositorioParticipacion;
    
    /**
     * Constructor por defecto.
     */
    public ServicioParticipacion() {
        this.repositorioParticipacion = new RepositorioParticipacion();
    }
    
    /**
     * Constructor con inyección de DAO para pruebas.
     */
    public ServicioParticipacion(RepositorioParticipacion repositorioParticipacion) {
        this.repositorioParticipacion = repositorioParticipacion;
    }

    /**
     * Guarda una nueva participación o actualiza una existente.
     * @param participacion Participación a guardar o actualizar
     * @return La participación guardada con su ID asignado
     */
    public Participacion guardar(Participacion participacion) {
        if (participacion == null) {
            throw new IllegalArgumentException("La participación no puede ser nula");
        }
        
        if (participacion.getEvento() == null) {
            throw new IllegalArgumentException("El evento de la participación no puede ser nulo");
        }
        
        if (participacion.getPersona() == null) {
            throw new IllegalArgumentException("La persona de la participación no puede ser nula");
        }
        
        if (participacion.getRol() == null) {
            throw new IllegalArgumentException("El rol de la participación no puede ser nulo");
        }
        
        return participacion.getId() == null ? 
               repositorioParticipacion.guardar(participacion) : 
               repositorioParticipacion.actualizar(participacion);
    }

    /**
     * Busca todas las participaciones.
     * @return Lista con todas las participaciones
     */
    public List<Participacion> buscarTodas() {
        return repositorioParticipacion.buscarTodas();
    }

    /**
     * Busca participaciones por el ID del evento.
     * @param eventoId ID del evento
     * @return Lista de participaciones del evento
     */
    public List<Participacion> buscarPorEvento(Long eventoId) {
        if (eventoId == null) {
            throw new IllegalArgumentException("El ID del evento no puede ser nulo");
        }
        return repositorioParticipacion.buscarPorEventoId(eventoId);
    }

    /**
     * Busca participaciones por el ID de la persona.
     * @param personaId ID de la persona
     * @return Lista de participaciones de la persona
     */
    public List<Participacion> buscarPorPersona(Long personaId) {
        if (personaId == null) {
            throw new IllegalArgumentException("El ID de la persona no puede ser nulo");
        }
        return repositorioParticipacion.buscarPorPersonaId(personaId);
    }

    /**
     * Elimina una participación por su ID.
     * @param id ID de la participación a eliminar
     * @return true si la participación fue eliminada, false si no existía
     */
    public boolean eliminar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la participación no puede ser nulo");
        }
        
        if (!repositorioParticipacion.existePorId(id)) {
            return false;
        }
        
        repositorioParticipacion.eliminarPorId(id);
        return true;
    }
}