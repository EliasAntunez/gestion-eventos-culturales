package com.gestioneventos.repositorio;

import com.gestioneventos.model.personas.Persona;
import com.gestioneventos.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones con la entidad Persona.
 * Maneja el acceso a datos para la entidad Persona.
 */
public class RepositorioPersona {

    /**
     * Guarda una nueva entidad Persona en la base de datos.
     * @param persona La persona a guardar
     * @return La persona guardada con su ID generado
     */
    public Persona guardar(Persona persona) {
        // Obtener el EntityManager para interactuar con la BD
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Iniciar una transacción de base de datos
            em.getTransaction().begin();
            // Persistir la entidad en la BD
            em.persist(persona);
            // Confirmar la transacción
            em.getTransaction().commit();
            // Devolver la entidad con su ID generado
            return persona;
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
     * @param persona La persona a actualizar
     * @return La persona actualizada
     */
    public Persona actualizar(Persona persona) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            // merge sincroniza el estado de la entidad con la BD
            Persona mergedPersona = em.merge(persona);
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
     * Guarda o actualiza una persona dependiendo si tiene id o no.
     * @param persona La persona a guardar o actualizar
     * @return La persona guardada o actualizada
     */
    public Persona guardarOActualizar(Persona persona) {
        return persona.getId() == null ? guardar(persona) : actualizar(persona);
    }

    /**
     * Busca una persona por su ID.
     * @param id ID de la persona a buscar
     * @return Optional con la persona si existe
     */
    public Optional<Persona> buscarPorId(Long id) {
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
    public List<Persona> buscarTodos() {
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
     * @param persona La persona a eliminar
     */
    public void eliminar(Persona persona) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            // Si la entidad no está gestionada por el EntityManager actual
            if (!em.contains(persona)) {
                // La buscamos para obtener una referencia gestionada
                persona = em.find(Persona.class, persona.getId());
                if (persona == null) {
                    return; // No existe, no hacemos nada
                }
            }
            // Eliminar la entidad de la BD
            em.remove(persona);
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
    public void eliminarPorId(Long id) {
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
    public boolean existePorId(Long id) {
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
    public long contarTotal() {
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
    public Optional<Persona> buscarPorDni(String dni) {
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
     * @param texto Texto a buscar
     * @return Lista de personas que coinciden con la búsqueda
     */
    public List<Persona> buscarPorNombreOApellido(String texto) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Consulta JPQL con búsqueda parcial (LIKE) e insensible a mayúsculas/minúsculas
            TypedQuery<Persona> query = em.createQuery(
                "SELECT p FROM Persona p WHERE " +
                "LOWER(p.nombre) LIKE LOWER(:query) OR " +
                "LOWER(p.apellido) LIKE LOWER(:query) " +
                "ORDER BY p.apellido, p.nombre", 
                Persona.class
            );
            // Establecer parámetro con % para búsqueda parcial
            query.setParameter("query", "%" + texto + "%");
            return query.getResultList();
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
            return buscarTodos(); // Si no hay texto de búsqueda, retorna todas las personas
        }
        return buscarPorNombreOApellido(texto);
    }
}