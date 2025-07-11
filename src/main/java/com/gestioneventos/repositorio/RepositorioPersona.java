package com.gestioneventos.repositorio;

import com.gestioneventos.model.personas.Persona;
import com.gestioneventos.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.hibernate.Hibernate;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones con la entidad Persona.
 * Hereda operaciones CRUD básicas de RepositorioBase.
 */
public class RepositorioPersona extends RepositorioBase<Persona, Long> {

    @Override
    protected boolean esNuevaEntidad(Persona persona) {
        return persona.getId() == null;
    }
    
    @Override
    protected void cargarRelaciones(EntityManager em, Persona persona) {
        // Inicializar colecciones para evitar LazyInitializationException
        Hibernate.initialize(persona.getParticipaciones());
    }
    
    /**
     * Busca una persona por su DNI.
     * @param dni DNI a buscar
     * @return Optional con la persona si existe
     */
    public Optional<Persona> buscarPorDni(String dni) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Persona> query = em.createQuery(
                "SELECT p FROM Persona p WHERE p.dni = :dni", 
                Persona.class
            );
            query.setParameter("dni", dni);
            
            try {
                Persona persona = query.getSingleResult();
                cargarRelaciones(em, persona);
                return Optional.of(persona);
            } catch (NoResultException e) {
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
        String jpql = "SELECT p FROM Persona p WHERE " +
                     "LOWER(p.nombre) LIKE LOWER(:texto) OR LOWER(p.apellido) LIKE LOWER(:texto) " +
                     "ORDER BY p.apellido, p.nombre";
        
        return ejecutarConsulta(jpql, "texto", "%" + texto + "%");
    }
    
    /**
     * Busca personas según el texto proporcionado.
     * Si el texto está vacío, retorna todas las personas.
     * @param texto Texto para buscar en nombre o apellido
     * @return Lista de personas que coinciden con la búsqueda
     */
    public List<Persona> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return buscarTodos();
        }
        return buscarPorNombreOApellido(texto);
    }
}