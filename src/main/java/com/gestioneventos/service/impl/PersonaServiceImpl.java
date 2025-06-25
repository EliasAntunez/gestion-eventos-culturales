package com.gestioneventos.service.impl;

import com.gestioneventos.dao.PersonaDAO;
import com.gestioneventos.dao.impl.PersonaDAOImpl;
import com.gestioneventos.model.personas.Persona;
import com.gestioneventos.service.PersonaService;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de personas.
 */
public class PersonaServiceImpl implements PersonaService {

    private final PersonaDAO personaDAO;
    
    public PersonaServiceImpl() {
        this.personaDAO = new PersonaDAOImpl();
    }
    
    public PersonaServiceImpl(PersonaDAO personaDAO) {
        this.personaDAO = personaDAO;
    }

    @Override
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
        
        return persona.getId() == null ? personaDAO.save(persona) : personaDAO.update(persona);
    }

    @Override
    public Optional<Persona> buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la persona no puede ser nulo");
        }
        return personaDAO.findById(id);
    }

    @Override
    public List<Persona> buscarTodas() {
        return personaDAO.findAll();
    }

    public List<Persona> buscarPorNombreOApellido(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("El texto de búsqueda no puede ser nulo o vacío");
        }
        return personaDAO.findByNombreOrApellido(texto);
    }

    // Implementación del método buscar de la interfaz
    @Override
    public List<Persona> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return buscarTodas(); // Si no hay texto de búsqueda, retorna todas las personas
        }
        return personaDAO.findByNombreOrApellido(texto);
    }

    @Override
    public Optional<Persona> buscarPorDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede ser nulo o vacío");
        }
        return personaDAO.findByDni(dni);
    }

    @Override
    public boolean eliminar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la persona no puede ser nulo");
        }
        
        if (!personaDAO.existsById(id)) {
            return false;
        }
        
        personaDAO.deleteById(id);
        return true;
    }

    // Implementación del método existeDniDuplicado
    @Override
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