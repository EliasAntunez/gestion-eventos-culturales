package com.gestioneventos.dao;

import com.gestioneventos.model.eventos.Evento;
import com.gestioneventos.model.eventos.EstadoEvento;
import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz para operaciones específicas de la entidad Evento.
 * Extiende GenericDAO para heredar las operaciones CRUD básicas.
 */
public interface EventoDAO extends GenericDAO<Evento, Long> {
    
    /**
     * Busca eventos por coincidencia parcial en el nombre.
     * @param nombre Texto a buscar en el nombre del evento
     * @return Lista de eventos que coinciden con el criterio
     */
    List<Evento> findByNombre(String nombre);
    
    /**
     * Busca eventos por su estado.
     * @param estado Estado del evento a buscar
     * @return Lista de eventos en el estado especificado
     */
    List<Evento> findByEstado(EstadoEvento estado);
    
    /**
     * Busca eventos que ocurren en una fecha específica.
     * @param fecha Fecha a buscar
     * @return Lista de eventos que ocurren en esa fecha
     */
    List<Evento> findByFecha(LocalDate fecha);
    
    /**
     * Busca eventos que ocurren entre dos fechas.
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de eventos dentro del rango de fechas
     */
    List<Evento> findByRangoFechas(LocalDate fechaInicio, LocalDate fechaFin);
    
    /**
     * Busca eventos en los que participa una persona específica.
     * @param personaId ID de la persona participante
     * @return Lista de eventos en los que participa la persona
     */
    List<Evento> findByParticipanteId(Long personaId);
}