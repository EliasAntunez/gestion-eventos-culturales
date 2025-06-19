package com.gestioneventos.dao;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz genérica para operaciones CRUD básicas.
 * @param <T> Tipo de entidad a manipular
 * @param <ID> Tipo del identificador de la entidad
 */
public interface GenericDAO<T, ID> {
    
    /**
     * Guarda una entidad en la base de datos.
     * @param entity Entidad a guardar
     * @return Entidad guardada
     */
    T save(T entity);
    
    /**
     * Actualiza una entidad existente.
     * @param entity Entidad a actualizar
     * @return Entidad actualizada
     */
    T update(T entity);
    
    /**
     * Busca una entidad por su ID.
     * @param id Identificador de la entidad
     * @return Optional con la entidad si existe, o vacío si no
     */
    Optional<T> findById(ID id);
    
    /**
     * Obtiene todas las entidades.
     * @return Lista de entidades
     */
    List<T> findAll();
    
    /**
     * Elimina una entidad.
     * @param entity Entidad a eliminar
     */
    void delete(T entity);
    
    /**
     * Elimina una entidad por su ID.
     * @param id Identificador de la entidad a eliminar
     */
    void deleteById(ID id);
    
    /**
     * Verifica si una entidad existe por su ID.
     * @param id Identificador a comprobar
     * @return true si existe, false en caso contrario
     */
    boolean existsById(ID id);
    
    /**
     * Cuenta el número total de entidades.
     * @return Número de entidades
     */
    long count();
}