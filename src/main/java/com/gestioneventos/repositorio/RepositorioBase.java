package com.gestioneventos.repositorio;

import com.gestioneventos.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

/**
 * Clase base abstracta para repositorios que proporcionan operaciones CRUD genéricas.
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
     * Guarda o actualiza una entidad dependiendo si tiene id o no.
     * @param entidad Entidad a guardar o actualizar
     * @return Entidad guardada o actualizada
     */
    public T guardarOActualizar(T entidad) {
        // La implementación depende de cómo se determine si una entidad es nueva o no
        // Esta implementación se deja a las subclases que conocen su estructura de ID
        if (esNuevaEntidad(entidad)) {
            return guardar(entidad);
        } else {
            return actualizar(entidad);
        }
    }
    
    /**
     * Verifica si una entidad es nueva (no tiene ID asignado)
     * @param entidad Entidad a verificar
     * @return true si la entidad es nueva, false si ya existe
     */
    protected abstract boolean esNuevaEntidad(T entidad);
    
    /**
     * Busca una entidad por su ID con eager loading de relaciones.
     * @param id Identificador de la entidad
     * @return Optional con la entidad si existe
     */
    public Optional<T> buscarPorId(ID id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            T entidad = em.find(entityClass, id);
            // Método de la subclase para cargar relaciones específicas
            if (entidad != null) {
                cargarRelaciones(em, entidad);
            }
            return Optional.ofNullable(entidad);
        } finally {
            em.close();
        }
    }
    
    /**
     * Método para cargar relaciones importantes (LazyLoading) de una entidad.
     * Las subclases deben implementar este método según sus necesidades.
     * @param em El EntityManager activo
     * @param entidad La entidad cuyas relaciones se cargarán
     */
    protected void cargarRelaciones(EntityManager em, T entidad) {
        // Por defecto no hace nada, las subclases deben sobrescribir si necesitan
        // cargar relaciones específicas para prevenir LazyInitializationException
    }
    
    /**
     * Obtiene todas las entidades.
     * @return Lista de entidades
     */
    public List<T> buscarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> rootEntry = cq.from(entityClass);
            CriteriaQuery<T> all = cq.select(rootEntry);
            
            TypedQuery<T> allQuery = em.createQuery(all);
            List<T> result = allQuery.getResultList();
            
            // Cargar relaciones para prevenir LazyInitializationException
            for (T entidad : result) {
                cargarRelaciones(em, entidad);
            }
            
            return result;
        } finally {
            em.close();
        }
    }
    
    /**
     * Busca entidades por un campo específico.
     * @param campo Nombre del campo a buscar
     * @param valor Valor a buscar
     * @return Lista de entidades que coinciden con la búsqueda
     */
    public List<T> buscarPor(String campo, Object valor) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e." + campo + " = :valor";
            TypedQuery<T> query = em.createQuery(jpql, entityClass);
            query.setParameter("valor", valor);
            List<T> result = query.getResultList();
            
            // Cargar relaciones para prevenir LazyInitializationException
            for (T entidad : result) {
                cargarRelaciones(em, entidad);
            }
            
            return result;
        } finally {
            em.close();
        }
    }
    
    /**
     * Busca entidades por coincidencia parcial en un campo string.
     * @param campo Nombre del campo a buscar
     * @param texto Texto a buscar (coincidencia parcial)
     * @return Lista de entidades que coinciden con la búsqueda
     */
    public List<T> buscarPorCoincidenciaParcial(String campo, String texto) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + 
                         " e WHERE LOWER(e." + campo + ") LIKE LOWER(:texto)";
            TypedQuery<T> query = em.createQuery(jpql, entityClass);
            query.setParameter("texto", "%" + texto + "%");
            List<T> result = query.getResultList();
            
            // Cargar relaciones para prevenir LazyInitializationException
            for (T entidad : result) {
                cargarRelaciones(em, entidad);
            }
            
            return result;
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
     * Ejecuta una consulta JPQL personalizada que devuelve entidades del tipo T.
     * @param jpql La consulta JPQL
     * @param params Los parámetros de la consulta como pares [nombre, valor]
     * @return Lista de entidades que cumplen la consulta
     */
    protected List<T> ejecutarConsulta(String jpql, Object... params) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<T> query = em.createQuery(jpql, entityClass);
            
            // Establecer los parámetros
            for (int i = 0; i < params.length; i += 2) {
                String paramName = (String) params[i];
                Object paramValue = params[i + 1];
                query.setParameter(paramName, paramValue);
            }
            
            List<T> result = query.getResultList();
            
            // Cargar relaciones para prevenir LazyInitializationException
            for (T entidad : result) {
                cargarRelaciones(em, entidad);
            }
            
            return result;
        } finally {
            em.close();
        }
    }
}