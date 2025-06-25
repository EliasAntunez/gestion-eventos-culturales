package com.gestioneventos.dao.impl;

import com.gestioneventos.dao.EventoDAO;
import com.gestioneventos.model.eventos.Evento;
import com.gestioneventos.model.eventos.EstadoEvento;
import com.gestioneventos.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del DAO para la entidad Evento.
 */
public class EventoDAOImpl implements EventoDAO {

    @Override
    public Evento save(Evento entity) {
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
    public Evento update(Evento entity) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Evento mergedEvento = em.merge(entity);
            em.getTransaction().commit();
            return mergedEvento;
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
    public Optional<Evento> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Evento evento = em.find(Evento.class, id);
            return Optional.ofNullable(evento);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Evento> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Evento> query = em.createQuery(
                "SELECT e FROM Evento e ORDER BY e.fechaInicio DESC", 
                Evento.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Evento entity) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (!em.contains(entity)) {
                entity = em.find(Evento.class, entity.getId());
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
            Evento evento = em.find(Evento.class, id);
            if (evento != null) {
                em.remove(evento);
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
            return em.find(Evento.class, id) != null;
        } finally {
            em.close();
        }
    }

    @Override
    public long count() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(e) FROM Evento e", Long.class);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Evento> findByNombre(String nombre) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Evento> query = em.createQuery(
                "SELECT e FROM Evento e WHERE LOWER(e.nombre) LIKE LOWER(:nombre) " +
                "ORDER BY e.fechaInicio DESC",
                Evento.class
            );
            query.setParameter("nombre", "%" + nombre + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Evento> findByEstado(EstadoEvento estado) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Evento> query = em.createQuery(
                "SELECT e FROM Evento e WHERE e.estadoEvento = :estado " +
                "ORDER BY e.fechaInicio DESC", 
                Evento.class
            );
            query.setParameter("estado", estado);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Evento> findByFecha(LocalDate fecha) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Buscar eventos que ocurren en la fecha especificada (su fecha de inicio 
            // es igual o anterior a la fecha dada y la fecha dada está dentro del 
            // período del evento considerando su duración)
            TypedQuery<Evento> query = em.createQuery(
                "SELECT e FROM Evento e WHERE " +
                "(e.fechaInicio <= :fecha AND " +
                "FUNCTION('DATE_ADD', e.fechaInicio, e.duracionEstimada, 'DAY') >= :fecha) " +
                "ORDER BY e.fechaInicio DESC",
                Evento.class
            );
            query.setParameter("fecha", fecha);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Evento> findByRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Buscar eventos que se superponen con el rango de fechas dado
            TypedQuery<Evento> query = em.createQuery(
                "SELECT e FROM Evento e WHERE " +
                "((e.fechaInicio >= :fechaInicio AND e.fechaInicio <= :fechaFin) OR " +
                "(FUNCTION('DATE_ADD', e.fechaInicio, e.duracionEstimada, 'DAY') >= :fechaInicio AND " +
                "FUNCTION('DATE_ADD', e.fechaInicio, e.duracionEstimada, 'DAY') <= :fechaFin) OR " +
                "(e.fechaInicio <= :fechaInicio AND " +
                "FUNCTION('DATE_ADD', e.fechaInicio, e.duracionEstimada, 'DAY') >= :fechaFin)) " +
                "ORDER BY e.fechaInicio DESC",
                Evento.class
            );
            query.setParameter("fechaInicio", fechaInicio);
            query.setParameter("fechaFin", fechaFin);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Evento> findByParticipanteId(Long personaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Evento> query = em.createQuery(
                "SELECT DISTINCT p.evento FROM Participacion p " +
                "WHERE p.persona.id = :personaId " +
                "ORDER BY p.evento.fechaInicio DESC", 
                Evento.class
            );
            query.setParameter("personaId", personaId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}