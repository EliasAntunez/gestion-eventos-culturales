package com.gestioneventos.repositorio;

import com.gestioneventos.model.participaciones.Participacion;
import com.gestioneventos.model.participaciones.RolParticipacion;
import com.gestioneventos.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones con la entidad Participacion.
 * Maneja el acceso a datos para la entidad Participacion.
 */
public class RepositorioParticipacion {

    /**
     * Guarda una nueva participación en la base de datos.
     * @param participacion La participación a guardar
     * @return La participación guardada con su ID generado
     */
    public Participacion guardar(Participacion participacion) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(participacion);
            em.getTransaction().commit();
            return participacion;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Actualiza una participación existente.
     * @param participacion La participación a actualizar
     * @return La participación actualizada
     */
    public Participacion actualizar(Participacion participacion) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Participacion mergedParticipacion = em.merge(participacion);
            em.getTransaction().commit();
            return mergedParticipacion;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Guarda o actualiza una participación dependiendo si tiene id o no.
     * @param participacion La participación a guardar o actualizar
     * @return La participación guardada o actualizada
     */
    public Participacion guardarOActualizar(Participacion participacion) {
        return participacion.getId() == null ? guardar(participacion) : actualizar(participacion);
    }

    /**
     * Busca una participación por su ID.
     * @param id ID de la participación a buscar
     * @return Optional con la participación si existe
     */
    public Optional<Participacion> buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Participacion participacion = em.find(Participacion.class, id);
            return Optional.ofNullable(participacion);
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene todas las participaciones ordenadas por fecha de inicio del evento.
     * @return Lista de todas las participaciones
     */
    public List<Participacion> buscarTodas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Participacion> query = em.createQuery(
                "SELECT p FROM Participacion p " +
                "ORDER BY p.evento.fechaInicio DESC", 
                Participacion.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Elimina una participación de la base de datos.
     * @param participacion La participación a eliminar
     */
    public void eliminar(Participacion participacion) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (!em.contains(participacion)) {
                participacion = em.find(Participacion.class, participacion.getId());
                if (participacion == null) {
                    return; // No existe, no hacemos nada
                }
            }
            em.remove(participacion);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Elimina una participación por su ID.
     * @param id ID de la participación a eliminar
     */
    public void eliminarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Participacion participacion = em.find(Participacion.class, id);
            if (participacion != null) {
                em.remove(participacion);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Verifica si existe una participación con el ID proporcionado.
     * @param id ID a verificar
     * @return true si la participación existe, false en caso contrario
     */
    public boolean existePorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Participacion.class, id) != null;
        } finally {
            em.close();
        }
    }

    /**
     * Busca participaciones asociadas a un evento específico.
     * @param eventoId ID del evento
     * @return Lista de participaciones del evento
     */
    public List<Participacion> buscarPorEventoId(Long eventoId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Participacion> query = em.createQuery(
                "SELECT p FROM Participacion p " +
                "WHERE p.evento.id = :eventoId " +
                "ORDER BY p.rol, p.persona.apellido, p.persona.nombre",
                Participacion.class
            );
            query.setParameter("eventoId", eventoId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Busca participaciones de una persona específica.
     * @param personaId ID de la persona
     * @return Lista de participaciones de la persona
     */
    public List<Participacion> buscarPorPersonaId(Long personaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Participacion> query = em.createQuery(
                "SELECT p FROM Participacion p " +
                "WHERE p.persona.id = :personaId " +
                "ORDER BY p.evento.fechaInicio DESC",
                Participacion.class
            );
            query.setParameter("personaId", personaId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Busca participaciones por evento y persona.
     * @param eventoId ID del evento
     * @param personaId ID de la persona
     * @return Lista de participaciones que cumplen ambos criterios
     */
    public List<Participacion> buscarPorEventoIdYPersonaId(Long eventoId, Long personaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Participacion> query = em.createQuery(
                "SELECT p FROM Participacion p " +
                "WHERE p.evento.id = :eventoId AND p.persona.id = :personaId",
                Participacion.class
            );
            query.setParameter("eventoId", eventoId);
            query.setParameter("personaId", personaId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Busca participaciones por evento y rol.
     * @param eventoId ID del evento
     * @param rol Rol de participación
     * @return Lista de participaciones en el evento con el rol especificado
     */
    public List<Participacion> buscarPorEventoIdYRol(Long eventoId, RolParticipacion rol) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Participacion> query = em.createQuery(
                "SELECT p FROM Participacion p " +
                "WHERE p.evento.id = :eventoId AND p.rol = :rol " +
                "ORDER BY p.persona.apellido, p.persona.nombre",
                Participacion.class
            );
            query.setParameter("eventoId", eventoId);
            query.setParameter("rol", rol);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}