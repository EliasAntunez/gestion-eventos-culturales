package com.gestioneventos.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase utilitaria para operaciones relacionadas con fechas.
 * Proporciona métodos para formatear y parsear fechas en diferentes formatos.
 */
public class DateUtils {
    
    // Formatos comunes para fechas
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    //private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Formatea un objeto LocalDate al formato dd/MM/yyyy.
     * 
     * @param date La fecha a formatear
     * @return String con la fecha formateada, o cadena vacía si date es null
     */
    public static String formatLocalDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMATTER.format(date);
    }
    
    /**
     * Formatea un objeto LocalDateTime al formato dd/MM/yyyy HH:mm.
     * 
     * @param dateTime La fecha y hora a formatear
     * @return String con la fecha y hora formateada, o cadena vacía si dateTime es null
     */
    public static String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return DATETIME_FORMATTER.format(dateTime);
    }

    /**
     * Convierte una cadena en formato dd/MM/yyyy a un objeto LocalDate.
     * 
     * @param dateStr La cadena que representa una fecha
     * @return LocalDate parseado o null si la cadena es inválida
     */
    public static LocalDate parseLocalDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Convierte una cadena en formato dd/MM/yyyy HH:mm a un objeto LocalDateTime.
     * 
     * @param dateTimeStr La cadena que representa una fecha y hora
     * @return LocalDateTime parseado o null si la cadena es inválida
     */
    public static LocalDateTime parseLocalDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Verifica si una fecha está dentro de un rango.
     * 
     * @param date La fecha a verificar
     * @param start Fecha de inicio del rango
     * @param end Fecha de fin del rango
     * @return true si la fecha está dentro del rango (inclusivo), false en caso contrario
     */
    public static boolean isDateInRange(LocalDate date, LocalDate start, LocalDate end) {
        if (date == null || start == null || end == null) {
            return false;
        }
        return !date.isBefore(start) && !date.isAfter(end);
    }
    
    /**
     * Calcula el número de días entre dos fechas.
     * 
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return Número de días entre las fechas, o -1 si alguna fecha es null o la fecha de fin es anterior a la de inicio
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            return -1;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
    }
    
    /**
     * Constructor privado para prevenir instanciación de esta clase utilitaria.
     */
    private DateUtils() {
        // Constructor privado para evitar instanciación
    }
}