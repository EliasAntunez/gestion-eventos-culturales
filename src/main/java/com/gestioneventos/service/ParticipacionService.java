package com.gestioneventos.service;

import com.gestioneventos.model.participaciones.Participacion;
import com.gestioneventos.model.participaciones.RolParticipacion;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz de servicio para operaciones sobre participaciones.
 */
public interface ParticipacionService {
    
    /**
     * Guarda una nueva participación o actualiza una existente.
     * @param participacion Participación a guardar o actualizar
     * @return La participación guardada con su ID asignado
     */
    Participacion guardar(Participacion participacion);
    
    /**
     * Busca una participación por su ID.
     * @param id ID de la participación a buscar
     * @return Optional con la participación si es encontrada, o vacío si no existe
     */
    Optional<Participacion> buscarPorId(Long id);
    
    /**
     * Busca todas las participaciones.
     * @return Lista con todas las participaciones
     */
    List<Participacion> buscarTodas();
    
    /**
     * Busca participaciones por el ID del evento.
     * @param eventoId ID del evento
     * @return Lista de participaciones del evento
     */
    List<Participacion> buscarPorEvento(Long eventoId);
    
    /**
     * Busca participaciones por el ID de la persona.
     * @param personaId ID de la persona
     * @return Lista de participaciones de la persona
     */
    List<Participacion> buscarPorPersona(Long personaId);
    
    /**
     * Busca participaciones por el ID del evento y un rol específico.
     * @param eventoId ID del evento
     * @param rol Rol de la participación
     * @return Lista de participaciones que cumplen ambos criterios
     */
    List<Participacion> buscarPorEventoYRol(Long eventoId, RolParticipacion rol);
    
    /**
     * Busca participaciones por evento y persona.
     * @param eventoId ID del evento
     * @param personaId ID de la persona
     * @return Lista de participaciones que cumplen ambos criterios
     */
    List<Participacion> buscarPorEventoYPersona(Long eventoId, Long personaId);
    
    /**
     * Elimina una participación por su ID.
     * @param id ID de la participación a eliminar
     * @return true si la participación fue eliminada, false si no existía
     */
    boolean eliminar(Long id);
}