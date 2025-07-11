package com.gestioneventos.repositorio;

import com.gestioneventos.model.participaciones.Participacion;
import com.gestioneventos.model.participaciones.RolParticipacion;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;

import java.util.List;

/**
 * Repositorio para operaciones con la entidad Participacion.
 * Hereda operaciones CRUD básicas de RepositorioBase.
 */
public class RepositorioParticipacion extends RepositorioBase<Participacion, Long> {

    @Override
    protected boolean esNuevaEntidad(Participacion participacion) {
        return participacion.getId() == null;
    }
    
    @Override
    protected void cargarRelaciones(EntityManager em, Participacion participacion) {
        // Inicializar relaciones para evitar LazyInitializationException
        Hibernate.initialize(participacion.getEvento());
        Hibernate.initialize(participacion.getPersona());
    }
    
    /**
     * Busca participaciones asociadas a un evento específico.
     * @param eventoId ID del evento
     * @return Lista de participaciones del evento
     */
    public List<Participacion> buscarPorEventoId(Long eventoId) {
        String jpql = "SELECT p FROM Participacion p " +
                     "WHERE p.evento.id = :eventoId " +
                     "ORDER BY p.rol, p.persona.apellido, p.persona.nombre";
        
        return ejecutarConsulta(jpql, "eventoId", eventoId);
    }
    
    /**
     * Busca participaciones de una persona específica.
     * @param personaId ID de la persona
     * @return Lista de participaciones de la persona
     */
    public List<Participacion> buscarPorPersonaId(Long personaId) {
        String jpql = "SELECT p FROM Participacion p " +
                     "WHERE p.persona.id = :personaId " +
                     "ORDER BY p.evento.fechaInicio DESC";
        
        return ejecutarConsulta(jpql, "personaId", personaId);
    }
    
    /**
     * Busca participaciones por evento y persona.
     * @param eventoId ID del evento
     * @param personaId ID de la persona
     * @return Lista de participaciones que cumplen ambos criterios
     */
    public List<Participacion> buscarPorEventoIdYPersonaId(Long eventoId, Long personaId) {
        String jpql = "SELECT p FROM Participacion p " +
                     "WHERE p.evento.id = :eventoId AND p.persona.id = :personaId";
        
        return ejecutarConsulta(jpql, "eventoId", eventoId, "personaId", personaId);
    }
    
    /**
     * Busca participaciones por evento y rol.
     * @param eventoId ID del evento
     * @param rol Rol de participación
     * @return Lista de participaciones en el evento con el rol especificado
     */
    public List<Participacion> buscarPorEventoIdYRol(Long eventoId, RolParticipacion rol) {
        String jpql = "SELECT p FROM Participacion p " +
                     "WHERE p.evento.id = :eventoId AND p.rol = :rol " +
                     "ORDER BY p.persona.apellido, p.persona.nombre";
        
        return ejecutarConsulta(jpql, "eventoId", eventoId, "rol", rol);
    }
    
    /**
     * Obtiene todas las participaciones ordenadas por fecha de inicio del evento.
     * @return Lista de todas las participaciones
     */
    public List<Participacion> buscarTodas() {
        return buscarTodos(); // Usa el método heredado
    }
}