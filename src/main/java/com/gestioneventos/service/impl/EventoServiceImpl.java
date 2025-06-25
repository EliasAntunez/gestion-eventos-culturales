package com.gestioneventos.service.impl;

import com.gestioneventos.model.eventos.Evento;
import com.gestioneventos.model.eventos.EstadoEvento;
import com.gestioneventos.model.personas.Persona;
import com.gestioneventos.model.participaciones.Participacion;
import com.gestioneventos.model.participaciones.RolParticipacion;
import com.gestioneventos.dao.EventoDAO;
import com.gestioneventos.dao.PersonaDAO;
import com.gestioneventos.dao.ParticipacionDAO;
import com.gestioneventos.dao.impl.EventoDAOImpl;
import com.gestioneventos.dao.impl.PersonaDAOImpl;
import com.gestioneventos.dao.impl.ParticipacionDAOImpl;
import com.gestioneventos.service.EventoService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de eventos que provee
 * la funcionalidad para gestionar los eventos culturales.
 */
public class EventoServiceImpl implements EventoService {

    private final EventoDAO eventoDAO;
    private final PersonaDAO personaDAO;
    private final ParticipacionDAO participacionDAO;
    
    /**
     * Constructor que inicializa los DAO necesarios.
     */
    public EventoServiceImpl() {
        this.eventoDAO = new EventoDAOImpl();
        this.personaDAO = new PersonaDAOImpl();
        this.participacionDAO = new ParticipacionDAOImpl();
    }
    
    /**
     * Constructor para inyección de dependencias (útil para testing).
     */
    public EventoServiceImpl(EventoDAO eventoDAO, PersonaDAO personaDAO, ParticipacionDAO participacionDAO) {
        this.eventoDAO = eventoDAO;
        this.personaDAO = personaDAO;
        this.participacionDAO = participacionDAO;
    }

    @Override
    public Evento guardar(Evento evento) {
        if (evento == null) {
            throw new IllegalArgumentException("El evento no puede ser nulo");
        }
        
        // Validar los campos obligatorios del evento
        validarEvento(evento);
        
        // Verificar si la fecha es válida para nuevos eventos
        if (evento.getFechaInicio().isBefore(LocalDate.now()) && evento.getId() == null) {
            throw new IllegalArgumentException("No se pueden crear eventos en fechas pasadas");
        }
        
        // Guardar o actualizar
        if (evento.getId() == null) {
            return eventoDAO.save(evento);
        } else {
            return eventoDAO.update(evento);
        }
    }
    
    @Override
    public Optional<Evento> buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del evento no puede ser nulo");
        }
        
        return eventoDAO.findById(id);
    }
    
    @Override
    public List<Evento> buscarTodos() {
        return eventoDAO.findAll();
    }
    
    @Override
    public boolean eliminar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del evento no puede ser nulo");
        }
        
        // Verificar si existe el evento
        Optional<Evento> eventoOpt = eventoDAO.findById(id);
        if (eventoOpt.isEmpty()) {
            return false;
        }
        
        // Eliminar las participaciones asociadas
        List<Participacion> participaciones = participacionDAO.findByEventoId(id);
        for (Participacion p : participaciones) {
            participacionDAO.delete(p);
        }
        
        // Eliminar el evento
        eventoDAO.deleteById(id);
        return true;
    }
    
    @Override
    public List<Evento> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de búsqueda no puede ser nulo o vacío");
        }
        
        return eventoDAO.findByNombre(nombre);
    }
    
    @Override
    public List<Evento> buscarPorFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha de búsqueda no puede ser nula");
        }
        
        return eventoDAO.findAll().stream()
                .filter(e -> estaEnFecha(e, fecha))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Evento> buscarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas del rango no pueden ser nulas");
        }
        
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        
        return eventoDAO.findAll().stream()
                .filter(e -> estaEnRangoFechas(e, fechaInicio, fechaFin))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Evento> buscarPorEstado(EstadoEvento estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado del evento no puede ser nulo");
        }
        
        return eventoDAO.findByEstado(estado);
    }
    
    @Override
    public Evento cambiarEstado(Long id, EstadoEvento nuevoEstado) {
        if (id == null || nuevoEstado == null) {
            throw new IllegalArgumentException("El ID del evento y el nuevo estado no pueden ser nulos");
        }
        
        Optional<Evento> eventoOpt = eventoDAO.findById(id);
        if (eventoOpt.isEmpty()) {
            throw new IllegalArgumentException("No existe un evento con el ID: " + id);
        }
        
        Evento evento = eventoOpt.get();
        evento.setEstadoEvento(nuevoEstado);
        
        return eventoDAO.update(evento);
    }
    
    @Override
    public Participacion agregarParticipacion(Long eventoId, Persona persona, RolParticipacion rol) {
        if (eventoId == null || persona == null || rol == null) {
            throw new IllegalArgumentException("El ID del evento, la persona y el rol no pueden ser nulos");
        }
        
        // Verificar que existe el evento
        Optional<Evento> eventoOpt = eventoDAO.findById(eventoId);
        if (eventoOpt.isEmpty()) {
            throw new IllegalArgumentException("No existe un evento con el ID: " + eventoId);
        }
        
        Evento evento = eventoOpt.get();
        
        // Verificar si el evento permite inscripciones
        if (rol == RolParticipacion.PARTICIPANTE && !evento.getPermiteInscripcion()) {
            throw new IllegalArgumentException("Este evento no permite inscripciones");
        }
        
        // Verificar si la persona ya está inscrita con el mismo rol
        List<Participacion> participacionesEvento = participacionDAO.findByEventoId(eventoId);
        for (Participacion p : participacionesEvento) {
            if (p.getPersona().getId().equals(persona.getId()) && p.getRol() == rol) {
                throw new IllegalArgumentException("La persona ya está inscrita en este evento con el mismo rol");
            }
        }
        
        // Guardar o recuperar la persona
        Persona personaGuardada;
        if (persona.getId() == null) {
            personaGuardada = personaDAO.save(persona);
        } else {
            Optional<Persona> personaOpt = personaDAO.findById(persona.getId());
            if (personaOpt.isEmpty()) {
                throw new IllegalArgumentException("No existe una persona con el ID: " + persona.getId());
            }
            personaGuardada = personaOpt.get();
        }
        
        // Crear y guardar la participación
        Participacion participacion = new Participacion(evento, personaGuardada, rol);
        return participacionDAO.save(participacion);
    }
    
    @Override
    public boolean eliminarParticipacion(Long eventoId, Long personaId) {
        if (eventoId == null || personaId == null) {
            throw new IllegalArgumentException("El ID del evento y de la persona no pueden ser nulos");
        }
        
        List<Participacion> participaciones = participacionDAO.findByEventoIdAndPersonaId(eventoId, personaId);
        if (participaciones.isEmpty()) {
            return false;
        }
        
        // Eliminar todas las participaciones encontradas
        for (Participacion p : participaciones) {
            participacionDAO.delete(p);
        }
        
        return true;
    }
    
    @Override
    public List<Participacion> obtenerParticipaciones(Long eventoId) {
        if (eventoId == null) {
            throw new IllegalArgumentException("El ID del evento no puede ser nulo");
        }
        
        return participacionDAO.findByEventoId(eventoId);
    }
    
    @Override
    public List<Evento> buscarPorParticipante(Long personaId) {
        if (personaId == null) {
            throw new IllegalArgumentException("El ID de la persona no puede ser nulo");
        }
        
        List<Participacion> participaciones = participacionDAO.findByPersonaId(personaId);
        List<Evento> eventos = new ArrayList<>();
        
        for (Participacion p : participaciones) {
            eventos.add(p.getEvento());
        }
        
        return eventos;
    }
    
    // Métodos auxiliares
    
    /**
     * Valida que los campos obligatorios del evento estén presentes.
     */
    private void validarEvento(Evento evento) {
        // Las validaciones ya están en los setters del modelo
        // Este método puede extenderse con validaciones adicionales de negocio
    }
    
    /**
     * Verifica si un evento está activo en una fecha específica.
     */
    private boolean estaEnFecha(Evento evento, LocalDate fecha) {
        LocalDate fechaInicio = evento.getFechaInicio();
        LocalDate fechaFin = fechaInicio.plusDays(evento.getDuracionEstimada());
        
        return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
    }
    
    /**
     * Verifica si un evento está activo en un rango de fechas.
     */
    private boolean estaEnRangoFechas(Evento evento, LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDate eventoInicio = evento.getFechaInicio();
        LocalDate eventoFin = eventoInicio.plusDays(evento.getDuracionEstimada());
        
        // Un evento está en el rango si:
        // - La fecha de inicio del evento está dentro del rango, o
        // - La fecha de fin del evento está dentro del rango, o
        // - El rango está contenido dentro de la duración del evento
        return (eventoInicio.isEqual(fechaInicio) || eventoInicio.isAfter(fechaInicio)) && 
               (eventoInicio.isEqual(fechaFin) || eventoInicio.isBefore(fechaFin)) ||
               (eventoFin.isEqual(fechaInicio) || eventoFin.isAfter(fechaInicio)) && 
               (eventoFin.isEqual(fechaFin) || eventoFin.isBefore(fechaFin)) ||
               (eventoInicio.isBefore(fechaInicio) && eventoFin.isAfter(fechaFin));
    }
}