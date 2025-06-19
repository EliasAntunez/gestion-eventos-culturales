// c:\2025 POO I - Trabajo Integrador\gestion-eventos-culturales\src\main\java\com\gestioneventos\service\PersonaService.java
package com.gestioneventos.service;

import com.gestioneventos.model.personas.Persona;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz del servicio para la gestión de personas.
 * Define las operaciones de negocio relacionadas con la entidad Persona.
 */
public interface PersonaService {
    
    /**
     * Guarda o actualiza una persona.
     * Si la persona tiene un ID, actualiza; de lo contrario, crea una nueva.
     * @param persona Persona a guardar o actualizar
     * @return Persona guardada o actualizada
     * @throws IllegalArgumentException Si la persona tiene datos inválidos
     */
    Persona guardar(Persona persona);
    
    /**
     * Obtiene todas las personas ordenadas por apellido y nombre.
     * @return Lista de personas
     */
    List<Persona> obtenerTodos();
    
    /**
     * Busca una persona por su ID.
     * @param id ID de la persona
     * @return Optional con la persona si existe, o vacío si no
     */
    Optional<Persona> obtenerPorId(Long id);
    
    /**
     * Busca una persona por su DNI.
     * @param dni DNI a buscar
     * @return Optional con la persona si existe, o vacío si no
     */
    Optional<Persona> obtenerPorDni(String dni);
    
    /**
     * Busca personas por nombre o apellido (búsqueda parcial).
     * Si el texto está vacío, devuelve todas las personas.
     * @param texto Texto para buscar en nombre o apellido
     * @return Lista de personas que coinciden con la búsqueda
     */
    List<Persona> buscar(String texto);
    
    /**
     * Elimina una persona por su ID.
     * @param id ID de la persona a eliminar
     * @return true si se eliminó correctamente, false si no existía
     */
    boolean eliminar(Long id);
    
    /**
     * Verifica si una persona con el DNI especificado ya existe.
     * Útil para validar que no existan DNIs duplicados al crear o actualizar.
     * @param dni DNI a verificar
     * @param idExcluido ID de persona a excluir de la verificación (útil para actualización)
     * @return true si existe otra persona con el mismo DNI, false en caso contrario
     */
    boolean existeDniDuplicado(String dni, Long idExcluido);
}