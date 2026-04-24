package com.autoloc.exception;

public class TechnicienNotFoundException extends RuntimeException {

    public TechnicienNotFoundException(Long id) {
        super("Technicien introuvable avec l'id : " + id);
    }
}