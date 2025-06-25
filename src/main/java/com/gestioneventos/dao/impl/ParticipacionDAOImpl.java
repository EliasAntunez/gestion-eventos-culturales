package com.gestioneventos.dao.impl;

import com.gestioneventos.dao.ParticipacionDAO;
import com.gestioneventos.model.participaciones.Participacion;
import com.gestioneventos.model.participaciones.RolParticipacion;
import com.gestioneventos.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del DAO para la entidad Participacion.
 */
public class ParticipacionDAOImpl implements ParticipacionDAO {

    @Override
    public Participacion save(Participacion entity) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return entity;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Participacion update(Participacion entity) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Participacion mergedParticipacion = em.merge(entity);
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

    @Override
    public Optional<Participacion> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Participacion participacion = em.find(Participacion.class, id);
            return Optional.ofNullable(participacion);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Participacion> findAll() {
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

    @Override
    public void delete(Participacion entity) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (!em.contains(entity)) {
                entity = em.find(Participacion.class, entity.getId());
                if (entity == null) {
                    return; // No existe, no hacemos nada
                }
            }
            em.remove(entity);
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

    @Override
    public void deleteById(Long id) {
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

    @Override
    public boolean existsById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Participacion.class, id) != null;
        } finally {
            em.close();
        }
    }

    @Override
    public long count() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(p) FROM Participacion p", Long.class);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Participacion> findByEventoId(Long eventoId) {
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

    @Override
    public List<Participacion> findByPersonaId(Long personaId) {
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

    @Override
    public List<Participacion> findByEventoIdAndPersonaId(Long eventoId, Long personaId) {
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

    @Override
    public List<Participacion> findByEventoIdAndRol(Long eventoId, RolParticipacion rol) {
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