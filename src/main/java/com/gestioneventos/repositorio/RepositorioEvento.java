package com.gestioneventos.repositorio;

import com.gestioneventos.model.eventos.Evento;
import com.gestioneventos.model.eventos.EstadoEvento;
import com.gestioneventos.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones con la entidad Evento.
 * Maneja el acceso a datos para la entidad Evento.
 */
public class RepositorioEvento {

    /**
     * Guarda un nuevo evento en la base de datos.
     * @param evento El evento a guardar
     * @return El evento guardado con su ID generado
     */
    public Evento guardar(Evento evento) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(evento);
            em.getTransaction().commit();
            return evento;
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
     * Actualiza un evento existente.
     * @param evento El evento a actualizar
     * @return El evento actualizado
     */
    public Evento actualizar(Evento evento) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Evento mergedEvento = em.merge(evento);
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

    /**
     * Guarda o actualiza un evento dependiendo si tiene id o no.
     * @param evento El evento a guardar o actualizar
     * @return El evento guardado o actualizado
     */
    public Evento guardarOActualizar(Evento evento) {
        return evento.getId() == null ? guardar(evento) : actualizar(evento);
    }

    /**
     * Busca un evento por su ID.
     * @param id ID del evento a buscar
     * @return Optional con el evento si existe
     */
    public Optional<Evento> buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Evento evento = em.find(Evento.class, id);
            return Optional.ofNullable(evento);
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene todos los eventos ordenados por fecha de inicio.
     * @return Lista de todos los eventos
     */
    public List<Evento> buscarTodos() {
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

    /**
     * Elimina un evento de la base de datos.
     * @param evento El evento a eliminar
     */
    public void eliminar(Evento evento) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (!em.contains(evento)) {
                evento = em.find(Evento.class, evento.getId());
                if (evento == null) {
                    return; // No existe, no hacemos nada
                }
            }
            em.remove(evento);
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
     * Elimina un evento por su ID.
     * @param id ID del evento a eliminar
     */
    public void eliminarPorId(Long id) {
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

    /**
     * Verifica si existe un evento con el ID proporcionado.
     * @param id ID a verificar
     * @return true si el evento existe, false en caso contrario
     */
    public boolean existePorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Evento.class, id) != null;
        } finally {
            em.close();
        }
    }

    /**
     * Cuenta la cantidad total de eventos en la base de datos.
     * @return Cantidad de eventos
     */
    public long contarTotal() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(e) FROM Evento e", Long.class);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Busca eventos por coincidencia parcial en el nombre.
     * @param nombre Texto a buscar en el nombre del evento
     * @return Lista de eventos que coinciden con el criterio
     */
    public List<Evento> buscarPorNombre(String nombre) {
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

    /**
     * Busca eventos por su estado.
     * @param estado Estado del evento a buscar
     * @return Lista de eventos en el estado especificado
     */
    public List<Evento> buscarPorEstado(EstadoEvento estado) {
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

    /**
     * Busca eventos que ocurren en una fecha específica.
     * @param fecha Fecha a buscar
     * @return Lista de eventos que ocurren en esa fecha
     */
    public List<Evento> buscarPorFecha(LocalDate fecha) {
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

    /**
     * Busca eventos que se superponen con un rango de fechas.
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de eventos dentro del rango de fechas
     */
    public List<Evento> buscarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
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

    /**
     * Busca eventos en los que participa una persona específica.
     * @param personaId ID de la persona participante
     * @return Lista de eventos en los que participa la persona
     */
    public List<Evento> buscarPorParticipanteId(Long personaId) {
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

    /**
     * Busca eventos según el texto proporcionado o todos si el texto está vacío.
     * @param texto Texto para buscar en el nombre del evento
     * @return Lista de eventos según el criterio de búsqueda
     */
    public List<Evento> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return buscarTodos();
        }
        return buscarPorNombre(texto);
    }
}