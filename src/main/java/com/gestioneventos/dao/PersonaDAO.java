package com.gestioneventos.dao;

import com.gestioneventos.model.personas.Persona;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones específicas de la entidad Persona.
 * Extiende GenericDAO para heredar las operaciones CRUD básicas.
 */
public interface PersonaDAO extends GenericDAO<Persona, Long> {
    
    /**
     * Busca una persona por su DNI.
     * @param dni DNI a buscar
     * @return Optional con la Persona si existe, o vacío si no
     */
    Optional<Persona> findByDni(String dni);

    //existsByDniExcludingId
    //boolean existsByDniExcludingId(String dni, Long idExcluir);

    /**
     * Busca personas por nombre y/o apellido (búsqueda parcial).
     * @param query Texto para buscar en nombre y apellido
     * @return Lista de personas que coinciden con la búsqueda
     */
    List<Persona> findByNombreOrApellido(String texto);
}