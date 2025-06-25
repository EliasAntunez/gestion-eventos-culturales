package com.gestioneventos.dao;

import com.gestioneventos.model.participaciones.Participacion;
import com.gestioneventos.model.participaciones.RolParticipacion;
import java.util.List;

/**
 * Interfaz para operaciones específicas de la entidad Participacion.
 * Extiende GenericDAO para heredar las operaciones CRUD básicas.
 */
public interface ParticipacionDAO extends GenericDAO<Participacion, Long> {
    
    /**
     * Busca participaciones asociadas a un evento específico.
     * @param eventoId ID del evento
     * @return Lista de participaciones del evento
     */
    List<Participacion> findByEventoId(Long eventoId);
    
    /**
     * Busca participaciones de una persona específica.
     * @param personaId ID de la persona
     * @return Lista de participaciones de la persona
     */
    List<Participacion> findByPersonaId(Long personaId);
    
    /**
     * Busca participaciones por evento y persona.
     * @param eventoId ID del evento
     * @param personaId ID de la persona
     * @return Lista de participaciones que cumplen ambos criterios
     */
    List<Participacion> findByEventoIdAndPersonaId(Long eventoId, Long personaId);
    
    /**
     * Busca participaciones por evento y rol.
     * @param eventoId ID del evento
     * @param rol Rol de participación
     * @return Lista de participaciones en el evento con el rol especificado
     */
    List<Participacion> findByEventoIdAndRol(Long eventoId, RolParticipacion rol);
}