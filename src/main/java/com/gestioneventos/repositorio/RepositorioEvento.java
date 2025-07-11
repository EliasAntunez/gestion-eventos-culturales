package com.gestioneventos.repositorio;

import com.gestioneventos.model.eventos.Evento;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;

import java.util.List;

/**
 * Repositorio para operaciones con la entidad Evento.
 * Hereda operaciones CRUD básicas de RepositorioBase.
 */
public class RepositorioEvento extends RepositorioBase<Evento, Long> {

    @Override
    protected boolean esNuevaEntidad(Evento evento) {
        return evento.getId() == null;
    }
    
    @Override
    protected void cargarRelaciones(EntityManager em, Evento evento) {
        // Inicializar colecciones para evitar LazyInitializationException
        Hibernate.initialize(evento.getParticipaciones());
    }
    
    /**
     * Busca eventos por coincidencia parcial en el nombre.
     * @param nombre Texto a buscar en el nombre del evento
     * @return Lista de eventos que coinciden con el criterio
     */
    public List<Evento> buscarPorNombre(String nombre) {
        return buscarPorCoincidenciaParcial("nombre", nombre);
    }
    
    /**
     * Busca eventos en los que participa una persona específica.
     * @param personaId ID de la persona participante
     * @return Lista de eventos en los que participa la persona
     */
    public List<Evento> buscarPorParticipanteId(Long personaId) {
        String jpql = "SELECT DISTINCT e FROM Evento e JOIN e.participaciones p " +
                      "WHERE p.persona.id = :personaId " +
                      "ORDER BY e.fechaInicio DESC";
        
        return ejecutarConsulta(jpql, "personaId", personaId);
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