// c:\2025 POO I - Trabajo Integrador\gestion-eventos-culturales\src\main\java\com\gestioneventos\dao\impl\PersonaDAOImpl.java
package com.gestioneventos.dao.impl;

import com.gestioneventos.dao.PersonaDAO;
import com.gestioneventos.model.personas.Persona;
import com.gestioneventos.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del DAO para la entidad Persona.
 */
public class PersonaDAOImpl implements PersonaDAO {

    /**
     * Guarda una nueva entidad Persona en la base de datos.
     * @param entity La persona a guardar
     * @return La persona guardada con su ID generado
     */
    @Override
    public Persona save(Persona entity) {
        // Obtener el EntityManager para interactuar con la BD
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Iniciar una transacción de base de datos
            em.getTransaction().begin();
            // Persistir la entidad en la BD
            em.persist(entity);
            // Confirmar la transacción
            em.getTransaction().commit();
            // Devolver la entidad con su ID generado
            return entity;
        } catch (Exception e) {
            // Si hay error, revertir la transacción
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            // Relanzar la excepción
            throw e;
        } finally {
            // Cerrar el EntityManager para liberar recursos
            em.close();
        }
    }

    /**
     * Actualiza una entidad Persona existente.
     * @param entity La persona a actualizar
     * @return La persona actualizada
     */
    @Override
    public Persona update(Persona entity) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            // merge sincroniza el estado de la entidad con la BD
            Persona mergedPersona = em.merge(entity);
            em.getTransaction().commit();
            return mergedPersona;
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
     * Busca una persona por su ID.
     * @param id ID de la persona a buscar
     * @return Optional con la persona si existe
     */
    @Override
    public Optional<Persona> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // find busca por clave primaria (ID)
            Persona persona = em.find(Persona.class, id);
            // Optional.ofNullable maneja el caso en que persona sea null
            return Optional.ofNullable(persona);
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene todas las personas ordenadas por apellido y nombre.
     * @return Lista de todas las personas
     */
    @Override
    public List<Persona> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Consulta JPQL para obtener todas las personas ordenadas
            TypedQuery<Persona> query = em.createQuery(
                "SELECT p FROM Persona p ORDER BY p.apellido, p.nombre", 
                Persona.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Elimina una persona de la base de datos.
     * @param entity La persona a eliminar
     */
    @Override
    public void delete(Persona entity) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            // Si la entidad no está gestionada por el EntityManager actual
            if (!em.contains(entity)) {
                // La buscamos para obtener una referencia gestionada
                entity = em.find(Persona.class, entity.getId());
                if (entity == null) {
                    return; // No existe, no hacemos nada
                }
            }
            // Eliminar la entidad de la BD
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

    /**
     * Elimina una persona por su ID.
     * @param id ID de la persona a eliminar
     */
    @Override
    public void deleteById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            // Buscar la persona por su ID
            Persona persona = em.find(Persona.class, id);
            if (persona != null) {
                // Si existe, eliminarla
                em.remove(persona);
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
     * Verifica si existe una persona con el ID proporcionado.
     * @param id ID a verificar
     * @return true si la persona existe, false en caso contrario
     */
    @Override
    public boolean existsById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Si find devuelve null, la persona no existe
            return em.find(Persona.class, id) != null;
        } finally {
            em.close();
        }
    }

    /**
     * Cuenta la cantidad total de personas en la base de datos.
     * @return Cantidad de personas
     */
    @Override
    public long count() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Consulta JPQL para contar las personas
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(p) FROM Persona p", Long.class);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Busca una persona por su DNI.
     * @param dni DNI a buscar
     * @return Optional con la persona si existe
     */
    @Override
    public Optional<Persona> findByDni(String dni) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Consulta JPQL con parámetro :dni
            TypedQuery<Persona> query = em.createQuery(
                "SELECT p FROM Persona p WHERE p.dni = :dni", 
                Persona.class
            );
            // Establecer el valor del parámetro
            query.setParameter("dni", dni);
            try {
                // Intentar obtener un único resultado
                Persona persona = query.getSingleResult();
                return Optional.of(persona);
            } catch (NoResultException e) {
                // Si no hay resultados, devolver Optional vacío
                return Optional.empty();
            }
        } finally {
            em.close();
        }
    }

    /**
     * Busca personas cuyo nombre o apellido contengan el texto proporcionado.
     * @param query Texto a buscar
     * @return Lista de personas que coinciden con la búsqueda
     */
    @Override
    public List<Persona> findByNombreOrApellido(String query) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Consulta JPQL con búsqueda parcial (LIKE) e insensible a mayúsculas/minúsculas
            TypedQuery<Persona> tQuery = em.createQuery(
                "SELECT p FROM Persona p WHERE " +
                "LOWER(p.nombre) LIKE LOWER(:query) OR " +
                "LOWER(p.apellido) LIKE LOWER(:query) " +
                "ORDER BY p.apellido, p.nombre", 
                Persona.class
            );
            // Establecer parámetro con % para búsqueda parcial
            tQuery.setParameter("query", "%" + query + "%");
            return tQuery.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Busca personas según el texto proporcionado.
     * Si el texto está vacío, retorna todas las personas.
     * @param texto Texto para buscar en nombre o apellido
     * @return Lista de personas que coinciden con la búsqueda
     */
    public List<Persona> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return findAll(); // Si no hay texto de búsqueda, retorna todas las personas
        }
        return findByNombreOrApellido(texto);
    }
    

    //
}