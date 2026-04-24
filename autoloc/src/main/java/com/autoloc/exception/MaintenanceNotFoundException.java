package com.autoloc.exception;

public class MaintenanceNotFoundException extends RuntimeException {

    public MaintenanceNotFoundException(Long id) {
        super("Ordre de maintenance introuvable avec l'id : " + id);
    }
}