// c:\2025 POO I - Trabajo Integrador\gestion-eventos-culturales\src\main\java\com\gestioneventos\service\impl\PersonaServiceImpl.java
package com.gestioneventos.service.impl;

import com.gestioneventos.dao.PersonaDAO;
import com.gestioneventos.dao.impl.PersonaDAOImpl;
import com.gestioneventos.model.personas.Persona;
import com.gestioneventos.service.PersonaService;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio para gestionar personas.
 * Actúa como intermediario entre los controladores y el DAO.
 */
public class PersonaServiceImpl implements PersonaService {
    
    // Referencia al DAO para operaciones de base de datos
    private final PersonaDAO personaDAO;

    /**
     * Constructor que inicializa el DAO para operaciones de persistencia.
     */
    public PersonaServiceImpl() {
        // Instanciamos la implementación concreta del DAO
        this.personaDAO = new PersonaDAOImpl();
    }

    /**
     * Guarda o actualiza una persona después de validar sus datos.
     * @param persona Persona a guardar o actualizar
     * @return Persona guardada o actualizada
     * @throws IllegalArgumentException Si la validación falla
     */
    @Override
    public Persona guardar(Persona persona) {
        // Validaciones adicionales antes de guardar
        validarPersona(persona);
        
        // Verificar si es una actualización o un nuevo registro
        if (persona.getId() != null) {
            // Si tiene ID, actualizamos
            return personaDAO.update(persona);
        } else {
            // Si no tiene ID, creamos nuevo
            return personaDAO.save(persona);
        }
    }

    /**
     * Obtiene todas las personas.
     * @return Lista de todas las personas
     */
    @Override
    public List<Persona> obtenerTodos() {
        return personaDAO.findAll();
    }

    /**
     * Busca una persona por su ID.
     * @param id ID de la persona
     * @return Optional con la persona si existe
     */
    @Override
    public Optional<Persona> obtenerPorId(Long id) {
        return personaDAO.findById(id);
    }

    /**
     * Busca una persona por su DNI.
     * @param dni DNI a buscar
     * @return Optional con la persona si existe
     */
    @Override
    public Optional<Persona> obtenerPorDni(String dni) {
        return personaDAO.findByDni(dni);
    }

    /**
     * Busca personas por nombre o apellido.
     * Si el texto está vacío, devuelve todas las personas.
     * @param texto Texto para buscar en nombre o apellido
     * @return Lista de personas que coinciden
     */
    @Override
    public List<Persona> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            // Si no hay texto de búsqueda, devolvemos todas
            return obtenerTodos();
        }
        // Si hay texto, realizamos la búsqueda filtrada
        return personaDAO.findByNombreOrApellido(texto);
    }

    /**
     * Elimina una persona por su ID.
     * @param id ID de la persona a eliminar
     * @return true si se eliminó, false si no existía
     */
    @Override
    public boolean eliminar(Long id) {
        // Primero verificamos que exista
        if (!personaDAO.existsById(id)) {
            return false;
        }
        // Si existe, la eliminamos
        personaDAO.deleteById(id);
        return true;
    }

    /**
     * Verifica si ya existe otra persona con el mismo DNI.
     * @param dni DNI a verificar
     * @param idExcluido ID de persona a excluir (para actualización)
     * @return true si existe duplicado, false en caso contrario
     */
    @Override
    public boolean existeDniDuplicado(String dni, Long idExcluido) {
        // Buscamos si existe una persona con ese DNI
        Optional<Persona> personaExistente = personaDAO.findByDni(dni);
        
        // Si no existe ninguna, no hay duplicado
        if (personaExistente.isEmpty()) {
            return false;
        }
        
        // Si encontró una persona con ese DNI, verificamos si es la misma que estamos editando
        // Si idExcluido es null o diferente al ID encontrado, significa que hay duplicado
        return idExcluido == null || !personaExistente.get().getId().equals(idExcluido);
    }
    
    /**
     * Valida que los datos de la persona sean correctos.
     * @param persona Persona a validar
     * @throws IllegalArgumentException Si la validación falla
     */
    private void validarPersona(Persona persona) {
        // Verificamos que la persona no sea nula
        if (persona == null) {
            throw new IllegalArgumentException("La persona no puede ser nula");
        }
        
        // Validar el nombre
        if (persona.getNombre() == null || persona.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        
        // Validar el apellido
        if (persona.getApellido() == null || persona.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido no puede estar vacío");
        }
        
        // Validar el DNI
        if (persona.getDni() == null || persona.getDni().trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede estar vacío");
        }
        
        // Verificar DNI duplicado (solo si no es la misma persona)
        if (existeDniDuplicado(persona.getDni(), persona.getId())) {
            throw new IllegalArgumentException("Ya existe una persona con el DNI: " + persona.getDni());
        }
    }
}