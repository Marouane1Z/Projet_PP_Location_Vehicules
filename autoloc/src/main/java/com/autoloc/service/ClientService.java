package com.autoloc.service;

import com.autoloc.model.Client;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
class ClientService {
    List<Client> clients = new ArrayList<>();
    ClientService(List<Client> clients) {
        this.clients = clients;
    }
    public List<Client> findAll() {
        return clients;
    }

    public void createClient(Client client){
        clients.add(client);
    }
    public void reserverVehicule(){
    }
    public void afficherVehicules(){
    }
    public void afficherReservations(){
    }
    public void annulerReservations(){
    }
    public void modifierReservations(){
    }
    public void reglerPaiement(){
    }
    public  void modifierInformations(){
    }

}
