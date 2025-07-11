package com.gestioneventos.service;

import com.gestioneventos.model.eventos.Evento;
import com.gestioneventos.model.eventos.EstadoEvento;
import com.gestioneventos.model.personas.Persona;
import com.gestioneventos.repositorio.RepositorioEvento;
import com.gestioneventos.repositorio.RepositorioParticipacion;
import com.gestioneventos.repositorio.RepositorioPersona;
import com.gestioneventos.model.participaciones.Participacion;
import com.gestioneventos.model.participaciones.RolParticipacion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de eventos culturales.
 */
public class ServicioEvento {

    private final RepositorioEvento repositorioEvento;
    private final RepositorioPersona repositorioPersona;
    private final RepositorioParticipacion repositorioParticipacion;
    
    /**
     * Constructor que inicializa los DAO necesarios.
     */
    public ServicioEvento() {
        this.repositorioEvento = new RepositorioEvento();
        this.repositorioPersona = new RepositorioPersona();
        this.repositorioParticipacion = new RepositorioParticipacion();
    }
    
    /**
     * Constructor para inyección de dependencias (útil para testing).
     */
    public ServicioEvento(RepositorioEvento repositorioEvento, RepositorioPersona repositorioPersona, RepositorioParticipacion repositorioParticipacion) {
        this.repositorioEvento = repositorioEvento;
        this.repositorioPersona = repositorioPersona;
        this.repositorioParticipacion = repositorioParticipacion;
    }

    /**
     * Guarda un nuevo evento o actualiza uno existente.
     * @param evento Evento a guardar o actualizar
     * @return El evento guardado con su ID asignado
     */
    public Evento guardar(Evento evento) {
        if (evento == null) {
            throw new IllegalArgumentException("El evento no puede ser nulo");
        }
        
        // Verificar si la fecha es válida para nuevos eventos
        if (evento.getFechaInicio().isBefore(LocalDate.now()) && evento.getId() == null) {
            throw new IllegalArgumentException("No se pueden crear eventos en fechas pasadas");
        }
        
        // Guardar o actualizar
        if (evento.getId() == null) {
            return repositorioEvento.guardar(evento);
        } else {
            return repositorioEvento.actualizar(evento);
        }
    }
    
    /**
     * Busca un evento por su ID.
     * @param id ID del evento a buscar
     * @return Optional con el evento si es encontrado, o vacío si no existe
     */
    public Optional<Evento> buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del evento no puede ser nulo");
        }
        
        return repositorioEvento.buscarPorId(id);
    }
    
    /**
     * Obtiene todos los eventos.
     * @return Lista con todos los eventos
     */
    public List<Evento> buscarTodos() {
        return repositorioEvento.buscarTodos();
    }
    
    /**
     * Elimina un evento por su ID.
     * @param id ID del evento a eliminar
     * @return true si el evento fue eliminado, false si no existía
     */
    public boolean eliminar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del evento no puede ser nulo");
        }
        
        // Verificar si existe el evento
        Optional<Evento> eventoOpt = repositorioEvento.buscarPorId(id);
        if (eventoOpt.isEmpty()) {
            return false;
        }
        
        // Eliminar las participaciones asociadas
        List<Participacion> participaciones = repositorioParticipacion.buscarPorEventoId(id);
        for (Participacion p : participaciones) {
            repositorioParticipacion.eliminar(p);
        }
        
        // Eliminar el evento
        repositorioEvento.eliminarPorId(id);
        return true;
    }
    
    /**
     * Busca eventos por nombre (coincidencia parcial).
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de eventos que coinciden con el criterio
     */
    public List<Evento> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de búsqueda no puede ser nulo o vacío");
        }
        
        return repositorioEvento.buscarPorNombre(nombre);
    }

    
    /**
     * Cambia el estado de un evento.
     * @param id ID del evento
     * @param nuevoEstado Nuevo estado para el evento
     * @return El evento actualizado
     */
    public Evento cambiarEstado(Long id, EstadoEvento nuevoEstado) {
        if (id == null || nuevoEstado == null) {
            throw new IllegalArgumentException("El ID del evento y el nuevo estado no pueden ser nulos");
        }
        
        Optional<Evento> eventoOpt = repositorioEvento.buscarPorId(id);
        if (eventoOpt.isEmpty()) {
            throw new IllegalArgumentException("No existe un evento con el ID: " + id);
        }
        
        Evento evento = eventoOpt.get();
        evento.setEstadoEvento(nuevoEstado);
        
        return repositorioEvento.actualizar(evento);
    }
    
    /**
     * Agrega una participación de una persona en un evento.
     * @param eventoId ID del evento
     * @param persona Persona que participará
     * @param rol Rol que tendrá la persona en el evento
     * @return La participación creada
     */
    public Participacion agregarParticipacion(Long eventoId, Persona persona, RolParticipacion rol) {
        if (eventoId == null || persona == null || rol == null) {
            throw new IllegalArgumentException("El ID del evento, la persona y el rol no pueden ser nulos");
        }
        
        // Verificar que existe el evento
        Optional<Evento> eventoOpt = repositorioEvento.buscarPorId(eventoId);
        if (eventoOpt.isEmpty()) {
            throw new IllegalArgumentException("No existe un evento con el ID: " + eventoId);
        }
        
        Evento evento = eventoOpt.get();
        
        // Verificar si el evento permite inscripciones
        if (rol == RolParticipacion.PARTICIPANTE && !evento.getPermiteInscripcion()) {
            throw new IllegalArgumentException("Este evento no permite inscripciones");
        }
        
        // Verificar si la persona ya está inscrita con el mismo rol
        List<Participacion> participacionesEvento = repositorioParticipacion.buscarPorEventoId(eventoId);
        for (Participacion p : participacionesEvento) {
            if (p.getPersona().getId().equals(persona.getId()) && p.getRol() == rol) {
                throw new IllegalArgumentException("La persona ya está inscrita en este evento con el mismo rol");
            }
        }
        
        // Guardar o recuperar la persona
        Persona personaGuardada;
        if (persona.getId() == null) {
            personaGuardada = repositorioPersona.guardar(persona);
        } else {
            Optional<Persona> personaOpt = repositorioPersona.buscarPorId(persona.getId());
            if (personaOpt.isEmpty()) {
                throw new IllegalArgumentException("No existe una persona con el ID: " + persona.getId());
            }
            personaGuardada = personaOpt.get();
        }
        
        // Crear y guardar la participación
        Participacion participacion = new Participacion(evento, personaGuardada, rol);
        return repositorioParticipacion.guardar(participacion);
    }
    
    /**
     * Elimina la participación de una persona en un evento.
     * @param eventoId ID del evento
     * @param personaId ID de la persona
     * @return true si se eliminó la participación, false si no existía
     */
    public boolean eliminarParticipacion(Long eventoId, Long personaId) {
        if (eventoId == null || personaId == null) {
            throw new IllegalArgumentException("El ID del evento y de la persona no pueden ser nulos");
        }
        
        List<Participacion> participaciones = repositorioParticipacion.buscarPorEventoIdYPersonaId(eventoId, personaId);
        if (participaciones.isEmpty()) {
            return false;
        }
        
        // Eliminar todas las participaciones encontradas
        for (Participacion p : participaciones) {
            repositorioParticipacion.eliminar(p);
        }
        
        return true;
    }
    
    /**
     * Obtiene todas las participaciones de un evento.
     * @param eventoId ID del evento
     * @return Lista de participaciones del evento
     */
    public List<Participacion> obtenerParticipaciones(Long eventoId) {
        if (eventoId == null) {
            throw new IllegalArgumentException("El ID del evento no puede ser nulo");
        }
        
        return repositorioParticipacion.buscarPorEventoId(eventoId);
    }
    
    /**
     * Busca eventos en los que participa una persona específica.
     * @param personaId ID de la persona
     * @return Lista de eventos en los que participa la persona
     */
    public List<Evento> buscarPorParticipante(Long personaId) {
        if (personaId == null) {
            throw new IllegalArgumentException("El ID de la persona no puede ser nulo");
        }
        
        List<Participacion> participaciones = repositorioParticipacion.buscarPorPersonaId(personaId);
        List<Evento> eventos = new ArrayList<>();
        
        for (Participacion p : participaciones) {
            eventos.add(p.getEvento());
        }
        
        return eventos;
    }
}