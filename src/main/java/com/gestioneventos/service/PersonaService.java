package com.gestioneventos.service;

import com.gestioneventos.model.personas.Persona;
import java.util.List;
import java.util.Optional;

public interface PersonaService {
    
    /**
     * Guarda una nueva persona o actualiza una existente.
     * @param persona Persona a guardar o actualizar
     * @return La persona guardada con su ID asignado
     */
    Persona guardar(Persona persona);
    
    /**
     * Busca una persona por su ID.
     * @param id ID de la persona a buscar
     * @return Optional con la persona si es encontrada, o vacío si no existe
     */
    Optional<Persona> buscarPorId(Long id);
    
    /**
     * Obtiene todas las personas.
     * @return Lista con todas las personas
     */
    List<Persona> buscarTodas();
    
    /**
     * Busca personas por coincidencia en nombre o apellido.
     * @param texto Texto a buscar en nombre o apellido
     * @return Lista de personas que coinciden con el criterio
     */
    List<Persona> buscar(String texto);
    
    /**
     * Busca una persona por su DNI.
     * @param dni DNI a buscar
     * @return Optional con la persona si es encontrada, o vacío si no existe
     */
    Optional<Persona> buscarPorDni(String dni);
    
    /**
     * Verifica si existe un DNI duplicado excluyendo a la persona con el ID proporcionado.
     * @param dni DNI a verificar
     * @param idExcluir ID de la persona a excluir de la verificación
     * @return true si existe un DNI duplicado, false en caso contrario
     */
    boolean existeDniDuplicado(String dni, Long idExcluir);
    
    /**
     * Elimina una persona por su ID.
     * @param id ID de la persona a eliminar
     * @return true si la persona fue eliminada, false si no existía
     */
    boolean eliminar(Long id);
}