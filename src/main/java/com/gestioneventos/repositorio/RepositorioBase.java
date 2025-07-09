package com.gestioneventos.repositorio;

import com.gestioneventos.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

/**
 * Clase base abstracta para repositorios que proporcionan operaciones CRUD genéricas.
 * Esta clase reemplaza el concepto de interfaz GenericDAO con una implementación concreta.
 * 
 * @param <T> Tipo de entidad a manipular
 * @param <ID> Tipo del identificador de la entidad
 */
public abstract class RepositorioBase<T, ID> {
    
    protected final Class<T> entityClass;
    
    /**
     * Constructor que utiliza reflexión para determinar el tipo de entidad.
     */
    @SuppressWarnings("unchecked")
    public RepositorioBase() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }
    
    /**
     * Guarda una nueva entidad en la base de datos.
     * @param entidad Entidad a guardar
     * @return Entidad guardada con su ID generado
     */
    public T guardar(T entidad) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entidad);
            em.getTransaction().commit();
            return entidad;
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
     * Actualiza una entidad existente.
     * @param entidad Entidad a actualizar
     * @return Entidad actualizada
     */
    public T actualizar(T entidad) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            T entidadActualizada = em.merge(entidad);
            em.getTransaction().commit();
            return entidadActualizada;
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
     * Busca una entidad por su ID.
     * @param id Identificador de la entidad
     * @return Optional con la entidad si existe
     */
    public Optional<T> buscarPorId(ID id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            T entidad = em.find(entityClass, id);
            return Optional.ofNullable(entidad);
        } finally {
            em.close();
        }
    }
    
    /**
     * Obtiene todas las entidades.
     * @return Lista de entidades
     */
    public List<T> buscarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Consulta JPQL genérica para obtener todas las entidades
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            TypedQuery<T> query = em.createQuery(jpql, entityClass);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Elimina una entidad.
     * @param entidad Entidad a eliminar
     */
    public void eliminar(T entidad) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (!em.contains(entidad)) {
                // La entidad no está gestionada, hay que obtenerla primero
                // Esto requiere que la entidad tenga un método getId()
                // Esta parte puede ser necesaria extenderla en clases hijas
                entidad = em.merge(entidad);
            }
            em.remove(entidad);
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
     * Elimina una entidad por su ID.
     * @param id Identificador de la entidad a eliminar
     */
    public void eliminarPorId(ID id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            T entidad = em.find(entityClass, id);
            if (entidad != null) {
                em.remove(entidad);
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
     * Verifica si una entidad existe por su ID.
     * @param id Identificador a comprobar
     * @return true si existe, false en caso contrario
     */
    public boolean existePorId(ID id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(entityClass, id) != null;
        } finally {
            em.close();
        }
    }
    
    /**
     * Cuenta el número total de entidades.
     * @return Número de entidades
     */
    public long contarTotal() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e";
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
    
    /**
     * Guarda o actualiza una entidad dependiendo si tiene id o no.
     * Esta implementación genérica debe ser extendida en subclases
     * para manejar correctamente la lógica de detección de ID.
     * 
     * @param entidad La entidad a guardar o actualizar
     * @return La entidad guardada o actualizada
     */
    public abstract T guardarOActualizar(T entidad);
}