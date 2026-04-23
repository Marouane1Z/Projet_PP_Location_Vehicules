package com.autoloc.service;


import com.autoloc.dto.PaiementRequest;
import com.autoloc.dto.PaiementResponse;
import org.springframework.stereotype.Service;


/*
* #Les fonctionnalité de service de paiement :
* Créer un paiement
* Rembourser un paiement (Par admin)
* Recuperer un paiement effectué
* Confirmer paiement espece (Par admin)
* Remboursement automatique ( declanché par reservationAnuule
*
*
* */
@Service
public class PaiementService {

    public PaiementResponse effectuerPaiement(Long reservationId, PaiementRequest paiementRequest) {
        // Verfier le status de la reservation

        //
    }

}
