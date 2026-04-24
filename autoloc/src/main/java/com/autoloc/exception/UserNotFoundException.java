package com.autoloc.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("Utilisateur avec l'id " + id + " introuvable");
    }

    public UserNotFoundException(String email) {
        super("Utilisateur avec l'email " + email + " introuvable");
    }
}