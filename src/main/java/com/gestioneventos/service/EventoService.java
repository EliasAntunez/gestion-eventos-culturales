package com.gestioneventos.service;

import com.gestioneventos.model.eventos.Evento;
import com.gestioneventos.model.eventos.EstadoEvento;
import com.gestioneventos.model.personas.Persona;
import com.gestioneventos.model.participaciones.Participacion;
import com.gestioneventos.model.participaciones.RolParticipacion;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones disponibles para gestionar eventos.
 */
public interface EventoService {

    /**
     * Guarda un nuevo evento o actualiza uno existente.
     * @param evento Evento a guardar o actualizar
     * @return El evento guardado con su ID asignado
     */
    Evento guardar(Evento evento);
    
    /**
     * Busca un evento por su ID.
     * @param id ID del evento a buscar
     * @return Optional con el evento si es encontrado, o vacío si no existe
     */
    Optional<Evento> buscarPorId(Long id);
    
    /**
     * Obtiene todos los eventos.
     * @return Lista con todos los eventos
     */
    List<Evento> buscarTodos();
    
    /**
     * Elimina un evento por su ID.
     * @param id ID del evento a eliminar
     * @return true si el evento fue eliminado, false si no existía
     */
    boolean eliminar(Long id);
    
    /**
     * Busca eventos por nombre (coincidencia parcial).
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de eventos que coinciden con el criterio
     */
    List<Evento> buscarPorNombre(String nombre);
    
    /**
     * Busca eventos programados para una fecha específica.
     * @param fecha Fecha a buscar
     * @return Lista de eventos en la fecha especificada
     */
    List<Evento> buscarPorFecha(LocalDate fecha);
    
    /**
     * Busca eventos que ocurren dentro de un rango de fechas.
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de eventos dentro del rango especificado
     */
    List<Evento> buscarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin);
    
    /**
     * Busca eventos por su estado.
     * @param estado Estado del evento a buscar
     * @return Lista de eventos en el estado especificado
     */
    List<Evento> buscarPorEstado(EstadoEvento estado);
    
    /**
     * Cambia el estado de un evento.
     * @param id ID del evento
     * @param nuevoEstado Nuevo estado para el evento
     * @return El evento actualizado
     */
    Evento cambiarEstado(Long id, EstadoEvento nuevoEstado);
    
    /**
     * Agrega una participación de una persona en un evento.
     * @param eventoId ID del evento
     * @param persona Persona que participará
     * @param rol Rol que tendrá la persona en el evento
     * @return La participación creada
     */
    Participacion agregarParticipacion(Long eventoId, Persona persona, RolParticipacion rol);
    
    /**
     * Elimina la participación de una persona en un evento.
     * @param eventoId ID del evento
     * @param personaId ID de la persona
     * @return true si se eliminó la participación, false si no existía
     */
    boolean eliminarParticipacion(Long eventoId, Long personaId);
    
    /**
     * Obtiene todas las participaciones de un evento.
     * @param eventoId ID del evento
     * @return Lista de participaciones del evento
     */
    List<Participacion> obtenerParticipaciones(Long eventoId);
    
    /**
     * Busca eventos en los que participa una persona específica.
     * @param personaId ID de la persona
     * @return Lista de eventos en los que participa la persona
     */
    List<Evento> buscarPorParticipante(Long personaId);
}