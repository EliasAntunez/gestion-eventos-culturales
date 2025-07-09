package com.gestioneventos.service;

import com.gestioneventos.model.personas.Persona;
import com.gestioneventos.repositorio.RepositorioPersona;

import java.util.List;
import java.util.Optional;

/**
 * Servicio unificado para la gestión de personas.
 */
public class ServicioPersona {

    private final RepositorioPersona repositorioPersona;
    
    /**
     * Constructor por defecto.
     */
    public ServicioPersona() {
        this.repositorioPersona = new RepositorioPersona();
    }
    
    /**
     * Constructor para inyección de dependencias (útil para testing).
     */
    public ServicioPersona(RepositorioPersona repositorioPersona) {
        this.repositorioPersona = repositorioPersona;
    }

    /**
     * Guarda una nueva persona o actualiza una existente.
     * @param persona Persona a guardar o actualizar
     * @return La persona guardada con su ID asignado
     */
    public Persona guardar(Persona persona) {
        if (persona == null) {
            throw new IllegalArgumentException("La persona no puede ser nula");
        }
        
        // Validar campos obligatorios
        validarPersona(persona);
        
        // Verificar si DNI ya existe (en caso de nueva persona)
        if (persona.getId() == null) {
            Optional<Persona> existente = buscarPorDni(persona.getDni());
            if (existente.isPresent()) {
                throw new IllegalArgumentException("Ya existe una persona con el DNI: " + persona.getDni());
            }
        }
        
        return persona.getId() == null ? repositorioPersona.guardar(persona) : repositorioPersona.actualizar(persona);
    }

    /**
     * Busca una persona por su ID.
     * @param id ID de la persona a buscar
     * @return Optional con la persona si es encontrada, o vacío si no existe
     */
    public Optional<Persona> buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la persona no puede ser nulo");
        }
        return repositorioPersona.buscarPorId(id);
    }
    
    /**
     * Obtiene todas las personas.
     * @return Lista con todas las personas
     */
    public List<Persona> buscarTodas() {
        return repositorioPersona.buscarTodos();
    }
    
    /**
     * Busca personas por coincidencia en nombre o apellido.
     * @param texto Texto a buscar en nombre o apellido
     * @return Lista de personas que coinciden con el criterio
     */
    public List<Persona> buscarPorNombreOApellido(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("El texto de búsqueda no puede ser nulo o vacío");
        }
        return repositorioPersona.buscarPorNombreOApellido(texto);
    }

    /**
     * Busca personas según el texto proporcionado.
     * Si el texto está vacío, retorna todas las personas.
     * @param texto Texto para buscar en nombre o apellido
     * @return Lista de personas que coinciden con la búsqueda
     */
    public List<Persona> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return buscarTodas(); // Si no hay texto de búsqueda, retorna todas las personas
        }
        return repositorioPersona.buscarPorNombreOApellido(texto);
    }
    
    /**
     * Busca una persona por su DNI.
     * @param dni DNI a buscar
     * @return Optional con la persona si es encontrada, o vacío si no existe
     */
    public Optional<Persona> buscarPorDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede ser nulo o vacío");
        }
        return repositorioPersona.buscarPorDni(dni);
    }
    
    /**
     * Elimina una persona por su ID.
     * @param id ID de la persona a eliminar
     * @return true si la persona fue eliminada, false si no existía
     */
    public boolean eliminar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la persona no puede ser nulo");
        }
        
        if (!repositorioPersona.existePorId(id)) {
            return false;
        }
        
        repositorioPersona.eliminarPorId(id);
        return true;
    }
    
    /**
     * Verifica si existe un DNI duplicado excluyendo a la persona con el ID proporcionado.
     * @param dni DNI a verificar
     * @param idExcluir ID de la persona a excluir de la verificación
     * @return true si existe un DNI duplicado, false en caso contrario
     */
    public boolean existeDniDuplicado(String dni, Long idExcluir) {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede ser nulo o vacío");
        }
        
        // Buscar persona con ese DNI
        Optional<Persona> personaEncontrada = buscarPorDni(dni);
        
        // Si no hay persona con ese DNI, no hay duplicado
        if (personaEncontrada.isEmpty()) {
            return false;
        }
        
        // Si hay persona con ese DNI, verificar si es la misma que estamos excluyendo
        Persona persona = personaEncontrada.get();
        return !persona.getId().equals(idExcluir);
    }

    /**
     * Valida que los campos obligatorios de la persona estén presentes.
     */
    private void validarPersona(Persona persona) {
        if (persona.getNombre() == null || persona.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la persona es obligatorio");
        }
        
        if (persona.getApellido() == null || persona.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido de la persona es obligatorio");
        }
        
        if (persona.getDni() == null || persona.getDni().trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI de la persona es obligatorio");
        }
    }
}