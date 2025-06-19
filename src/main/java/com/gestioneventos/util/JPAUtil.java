package com.gestioneventos.util;

// Importaciones necesarias para JPA
import jakarta.persistence.EntityManager;         // Para manejar entidades y operaciones CRUD
import jakarta.persistence.EntityManagerFactory;  // Para crear instancias de EntityManager
import jakarta.persistence.Persistence;           // Para crear EntityManagerFactory usando persistence.xml

/**
 * Clase utilitaria para gestionar la conexión con la base de datos mediante JPA.
 * Implementa el patrón Singleton para mantener una única instancia de EntityManagerFactory.
 */
public class JPAUtil {
    
    // Nombre de la unidad de persistencia definida en persistence.xml
    private static final String PERSISTENCE_UNIT_NAME = "EventosPU";
    
    // Instancia única de EntityManagerFactory para toda la aplicación (patrón Singleton)
    private static EntityManagerFactory factory;
    
    /**
     * Obtiene una instancia de EntityManager para realizar operaciones con la base de datos.
     * Si es la primera vez que se llama, inicializa la fábrica.
     * 
     * @return una instancia de EntityManager lista para ser utilizada
     */
    public static EntityManager getEntityManager() {
        // Verifica si la fábrica ya fue inicializada
        if (factory == null || !factory.isOpen()) {
            // Crea la fábrica usando el nombre de la unidad de persistencia definido en persistence.xml
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        // Crea y retorna un nuevo EntityManager desde la fábrica
        return factory.createEntityManager();
    }
    
    /**
     * Cierra la fábrica de EntityManager y libera los recursos.
     * Debe llamarse cuando la aplicación se cierre.
     */
    public static void shutdown() {
        // Verifica si la fábrica existe y está abierta antes de intentar cerrarla
        if (factory != null && factory.isOpen()) {
            // Cierra la fábrica y libera las conexiones y recursos asociados
            factory.close();
            // Establece la referencia a null para que pueda ser recolectada por el garbage collector
            factory = null;
        }
    }
    
    /**
     * Constructor privado para prevenir instanciación de esta clase utilitaria.
     * Al ser todos los métodos estáticos, no se requiere crear instancias.
     */
    private JPAUtil() {
        // Constructor privado para evitar instanciación
    }
}