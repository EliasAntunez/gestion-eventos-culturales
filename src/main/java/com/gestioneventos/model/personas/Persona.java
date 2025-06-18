package com.gestioneventos.model.personas;

import jakarta.persistence.*;
import com.gestioneventos.model.participaciones.Participacion;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "personas")
public class Persona {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String nombre;
    
    @Column(nullable = false, length = 50)
    private String apellido;
    
    @Column(nullable = false, unique = true, length = 20)
    private String dni;
    
    @Column(length = 20)
    private String telefono;
    
    @Column(length = 100)
    private String email;
    
    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Participacion> participaciones = new ArrayList<>();
    
    // Constructor vacío requerido por JPA
    protected Persona() {
    }
    
    public Persona(String nombre, String apellido, String dni, String telefono, String email) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
    }
    
    public Long getId() {
        return id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
        }
        this.nombre = nombre;
    }
    
    public String getApellido() {
        return apellido;
    }
    
    public void setApellido(String apellido) {
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido no puede ser nulo o vacío");
        }
        this.apellido = apellido;
    }
    
    public String getDni() {
        return dni;
    }
    
    public void setDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede ser nulo o vacío");
        }
        this.dni = dni;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public List<Participacion> getParticipaciones() {
        return new ArrayList<>(participaciones);
    }
    
    @Override
    public String toString() {
        return nombre + " " + apellido + " (DNI: " + dni + ")";
    }
}