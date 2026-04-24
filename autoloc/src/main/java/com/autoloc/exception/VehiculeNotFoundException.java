package com.autoloc.exception;

public class VehiculeNotFoundException extends RuntimeException {

    public VehiculeNotFoundException(Long id) {
        super("Véhicule introuvable avec l'id : " + id);
    }

    public VehiculeNotFoundException(String immatriculation) {
        super("Véhicule introuvable avec l'immatriculation : " + immatriculation);
    }
}