package com.gestioneventos.model.eventos;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ferias")
public class Feria extends Evento {
    
    @Column(name = "cantidad_stands")
    private int cantidadStands;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ubicacion")
    private TipoUbicacion tipoUbicacion;
    
    // Constructor vacío requerido por JPA
    protected Feria() {
        super();
    }
    
    // Constructor con los atributos específicos
    public Feria(String nombre, LocalDate fechaInicio, int duracionEstimada,
                String ubicacion, int capacidadMaxima, double costoBase,
                int cantidadStands, TipoUbicacion tipoUbicacion) {
        super(nombre, fechaInicio, duracionEstimada, ubicacion, capacidadMaxima, costoBase);
        this.cantidadStands = cantidadStands;
        this.tipoUbicacion = tipoUbicacion;
    }
    
    public int getCantidadStands() {
        return cantidadStands;
    }
    
    public void setCantidadStands(int cantidadStands) {
        if (cantidadStands <= 0) {
            throw new IllegalArgumentException("La cantidad de stands debe ser un valor positivo");
        }
        this.cantidadStands = cantidadStands;
    }
    
    public TipoUbicacion getTipoUbicacion() {
        return tipoUbicacion;
    }
    
    public void setTipoUbicacion(TipoUbicacion tipoUbicacion) {
        if (tipoUbicacion == null) {
            throw new IllegalArgumentException("El tipo de ubicación no puede ser nulo");
        }
        this.tipoUbicacion = tipoUbicacion;
    }
    
    @Override
    public String obtenerDescripcionEspecifica() {
        return "Feria " + getNombre() + " - " + cantidadStands + " stands, " + 
               (tipoUbicacion == TipoUbicacion.AL_AIRE_LIBRE ? "al aire libre" : "techado");
    }
    
    @Override
    public double calcularCostoTotal() {
        double costoBase = getCostoBase();
        // El costo adicional depende del número de stands y del tipo de ubicación
        double costoStands = 500.0 * cantidadStands;
        double costoUbicacion = (tipoUbicacion == TipoUbicacion.TECHADO) ? 10000.0 : 5000.0;
        return costoBase + costoStands + costoUbicacion;
    }
    
    @Override
    public void validarRequisitosEspecificos() {
        // Para ferias, la cantidad de stands no puede ser mayor que la capacidad del lugar
        if (cantidadStands > getCapacidadMaxima() / 10) { // Asumimos que cada stand ocupa espacio para 10 personas
            throw new IllegalStateException("La cantidad de stands excede la capacidad del lugar");
        }
    }
}