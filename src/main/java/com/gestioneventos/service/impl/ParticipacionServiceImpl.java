package com.gestioneventos.service.impl;

import com.gestioneventos.dao.ParticipacionDAO;
import com.gestioneventos.dao.impl.ParticipacionDAOImpl;
import com.gestioneventos.model.participaciones.Participacion;
import com.gestioneventos.model.participaciones.RolParticipacion;
import com.gestioneventos.service.ParticipacionService;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de participaciones.
 */
public class ParticipacionServiceImpl implements ParticipacionService {

    private final ParticipacionDAO participacionDAO;
    
    /**
     * Constructor por defecto.
     */
    public ParticipacionServiceImpl() {
        this.participacionDAO = new ParticipacionDAOImpl();
    }
    
    /**
     * Constructor con inyección de DAO para pruebas.
     */
    public ParticipacionServiceImpl(ParticipacionDAO participacionDAO) {
        this.participacionDAO = participacionDAO;
    }

    @Override
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
               participacionDAO.save(participacion) : 
               participacionDAO.update(participacion);
    }

    @Override
    public Optional<Participacion> buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la participación no puede ser nulo");
        }
        return participacionDAO.findById(id);
    }

    @Override
    public List<Participacion> buscarTodas() {
        return participacionDAO.findAll();
    }

    @Override
    public List<Participacion> buscarPorEvento(Long eventoId) {
        if (eventoId == null) {
            throw new IllegalArgumentException("El ID del evento no puede ser nulo");
        }
        return participacionDAO.findByEventoId(eventoId);
    }

    @Override
    public List<Participacion> buscarPorPersona(Long personaId) {
        if (personaId == null) {
            throw new IllegalArgumentException("El ID de la persona no puede ser nulo");
        }
        return participacionDAO.findByPersonaId(personaId);
    }
    
    @Override
    public List<Participacion> buscarPorEventoYRol(Long eventoId, RolParticipacion rol) {
        if (eventoId == null) {
            throw new IllegalArgumentException("El ID del evento no puede ser nulo");
        }
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        return participacionDAO.findByEventoIdAndRol(eventoId, rol);
    }
    
    @Override
    public List<Participacion> buscarPorEventoYPersona(Long eventoId, Long personaId) {
        if (eventoId == null) {
            throw new IllegalArgumentException("El ID del evento no puede ser nulo");
        }
        if (personaId == null) {
            throw new IllegalArgumentException("El ID de la persona no puede ser nulo");
        }
        return participacionDAO.findByEventoIdAndPersonaId(eventoId, personaId);
    }

    @Override
    public boolean eliminar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la participación no puede ser nulo");
        }
        
        if (!participacionDAO.existsById(id)) {
            return false;
        }
        
        participacionDAO.deleteById(id);
        return true;
    }
}