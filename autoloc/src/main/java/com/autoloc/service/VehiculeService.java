package com.autoloc.service;

import com.autoloc.dto.VehiculeRequest;
import com.autoloc.dto.VehiculeResponse;
import com.autoloc.enums.statutVehicule;
import com.autoloc.exception.VehiculeNotFoundException;
import com.autoloc.model.Camion;
import com.autoloc.model.Option;
import com.autoloc.model.Vehicule;
import com.autoloc.model.Voiture;
import com.autoloc.repository.OptionRepository;
import com.autoloc.repository.VehiculeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Vehicule — toute la logique métier liée aux véhicules.
 *
 * Règles métier appliquées :
 *   - immatriculation unique
 *   - statut DISPONIBLE par défaut à la création
 *   - impossible de supprimer un véhicule LOUE ou EN_MAINTENANCE
 *   - le type (VOITURE/CAMION) ne change jamais après création
 */

@Service
@RequiredArgsConstructor
@Transactional
public class VehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final OptionRepository   optionRepository;

    // ─── AJOUTER ──────────────────────────────────────────────────────────

    /**
     * Construit un objet Voiture ou Camion selon le champ type du DTO.
     * Initialise tous les champs communs hérités de Vehicule.
     */

    private Vehicule construireVehicule(VehiculeRequest request) {
        Vehicule vehicule;

        if ("VOITURE".equals(request.getType())) {
            Voiture voiture = new Voiture();
            voiture.setNbPortes(request.getNbPortes());
            voiture.setNbPlaces(request.getNbPlaces());
            voiture.setCategorie(request.getCategorie());
            vehicule = voiture;

        } else if ("CAMION".equals(request.getType())) {
            Camion camion = new Camion();
            camion.setTonnage(request.getTonnage());
            camion.setVolume(request.getVolume());
            camion.setLongueur(request.getLongueur());
            camion.setElevator(
                    request.getElevator() != null ? request.getElevator() : false
            );
            vehicule = camion;

        } else {
            throw new IllegalArgumentException(
                    "Type de véhicule invalide : " + request.getType()
                            + ". Valeurs acceptées : VOITURE, CAMION"
            );
        }

        // Champs communs (hérités de Vehicule)
        vehicule.setMarque(request.getMarque());
        vehicule.setModele(request.getModele());
        vehicule.setImmatriculation(request.getImmatriculation());
        vehicule.setAnnee(request.getAnnee());
        vehicule.setPrixParJour(request.getPrixParJour());
        vehicule.setCaution(request.getCaution());
        vehicule.setImage(request.getImage());
        vehicule.setTypeCarburant(request.getTypeCarburant());
        vehicule.setTypeBoiteVitesse(request.getTypeBoite());

        // Statut par défaut : DISPONIBLE
        vehicule.setStatut(statutVehicule.DISPONIBLE);

        return vehicule;
    }

    /**
     * Associe les options au véhicule.
     * Si optionIds est null ou vide → on vide la liste des options.
     */

    private void assignerOptions(Vehicule vehicule, List<Long> optionIds) {
        vehicule.getOptions().clear();
        if (optionIds != null && !optionIds.isEmpty()) {
            List<Option> options = optionRepository.findAllById(optionIds);
            vehicule.getOptions().addAll(options);
        }
    }

    public VehiculeResponse ajouterVehicule(VehiculeRequest request) {

        // Règle métier : immatriculation unique
        if (vehiculeRepository.existsByImmatriculation(request.getImmatriculation())) {
            throw new IllegalArgumentException(
                    "Immatriculation déjà existante : " + request.getImmatriculation()
            );
        }

        // Construire Voiture ou Camion selon le type
        Vehicule vehicule = construireVehicule(request);

        // Assigner les options (relation M:N)
        assignerOptions(vehicule, request.getOptionIds());

        return toResponse(vehiculeRepository.save(vehicule));
    }

    // ─── LIRE ─────────────────────────────────────────────────────────────

    // cherche un vehicule par ID
    @Transactional(readOnly = true)  //cette méthode lit uniquement
    public VehiculeResponse findById(Long id) {
        return toResponse(
                vehiculeRepository.findById(id)
                        .orElseThrow(() -> new VehiculeNotFoundException(id))
        );
    }

    // cherche tous les vehicules
    @Transactional(readOnly = true)
    public List<VehiculeResponse> findAll() {
        return vehiculeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /*@Transactional(readOnly = true)
    public List<VehiculeResponse> findDisponibles() {
        return vehiculeRepository.findByStatut(StatutVehicule.DISPONIBLE)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }**/

    /* @Transactional(readOnly = true)
    public List<VehiculeResponse> findDisponiblesSurPeriode(
            LocalDate dateDebut, LocalDate dateFin) {
        return vehiculeRepository.findDisponiblesSurPeriode(dateDebut, dateFin)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }**/


    @Transactional(readOnly = true)
    public List<VehiculeResponse> findAllVoitures() {
        return vehiculeRepository.findByType("VOITURE")
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VehiculeResponse> findAllCamions() {
        return vehiculeRepository.findByType("CAMION")
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Chercher par immatriculation
    @Transactional(readOnly = true)
    public VehiculeResponse findByImmatriculation(String immatriculation) {
        return toResponse(
                vehiculeRepository.findByImmatriculation(immatriculation)
                        .orElseThrow(() -> new VehiculeNotFoundException(
                                "Véhicule introuvable avec l'immatriculation : " + immatriculation
                        ))
        );
    }

    // Chercher par marque
    @Transactional(readOnly = true)
    public List<VehiculeResponse> findByMarque(String marque) {
        return vehiculeRepository.findByMarqueIgnoreCase(marque)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Chercher par modele
    @Transactional(readOnly = true)
    public List<VehiculeResponse> findByModele(String modele) {
        return vehiculeRepository.findByModeleIgnoreCase(modele)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // recherche par mot clé
    @Transactional(readOnly = true)
    public List<VehiculeResponse> rechercher(String keyword) {
        return vehiculeRepository
                .findByMarqueContainingIgnoreCaseOrModeleContainingIgnoreCaseOrImmatriculationContainingIgnoreCase(
                        keyword,  // ← pour marque
                        keyword,  // ← pour modele
                        keyword   // ← pour immatriculation
                )
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


    // ─── MODIFIER ─────────────────────────────────────────────────────────

    public VehiculeResponse modifier(Long id, VehiculeRequest request) {

        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new VehiculeNotFoundException(id));

        // Vérifier unicité immatriculation si elle existe déjà ou pas
        if (!vehicule.getImmatriculation().equals(request.getImmatriculation())
                && vehiculeRepository.existsByImmatriculation(request.getImmatriculation())) {
            throw new IllegalArgumentException(
                    "Immatriculation déjà existante : " + request.getImmatriculation()
            );
        }

        // Mise à jour champs communs
        vehicule.setMarque(request.getMarque());
        vehicule.setModele(request.getModele());
        vehicule.setImmatriculation(request.getImmatriculation());
        vehicule.setAnnee(request.getAnnee());
        vehicule.setPrixParJour(request.getPrixParJour());
        vehicule.setCaution(request.getCaution());
        vehicule.setImage(request.getImage());
        vehicule.setTypeCarburant(request.getTypeCarburant());
        vehicule.setTypeBoiteVitesse(request.getTypeBoite());

        // Mise à jour champs spécifiques
        if (vehicule instanceof Voiture voiture) {
            voiture.setNbPortes(request.getNbPortes());
            voiture.setNbPlaces(request.getNbPlaces());
            voiture.setCategorie(request.getCategorie());
        } else if (vehicule instanceof Camion camion) {
            camion.setTonnage(request.getTonnage());
            camion.setVolume(request.getVolume());
            camion.setLongueur(request.getLongueur());
            camion.setElevator(
                    request.getElevator() != null ? request.getElevator() : false
            );
        }

        assignerOptions(vehicule, request.getOptionIds());

        return toResponse(vehiculeRepository.save(vehicule));
    }

    // ─── CHANGER STATUT ───────────────────────────────────────────────────

    /**
     * Change le statut d'un véhicule.
     * Appelé par MaintenanceService et ReservationService.
     */
    public VehiculeResponse changerStatut(Long id, statutVehicule nouveauStatut) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new VehiculeNotFoundException(id));
        vehicule.changerStatut(nouveauStatut);
        return toResponse(vehiculeRepository.save(vehicule));
    }

    // ─── SUPPRIMER ────────────────────────────────────────────────────────

    public void supprimer(Long id) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new VehiculeNotFoundException(id));

        // Règle métier : impossible de supprimer si LOUE
        if (vehicule.getStatut() == statutVehicule.LOUE) {
            throw new IllegalStateException(
                    "Impossible de supprimer un véhicule en cours de location"
            );
        }

        // Règle métier : impossible de supprimer si EN_MAINTENANCE
        if (vehicule.getStatut() == statutVehicule.EN_MAINTENANCE) {
            throw new IllegalStateException(
                    "Impossible de supprimer un véhicule en maintenance"
            );
        }

        vehiculeRepository.delete(vehicule);
    }

    // ─── MÉTHODES PRIVÉES ─────────────────────────────────────────────────



    /**
     * Convertit une entité Vehicule en VehiculeResponse DTO.
     * Évite la boucle infinie JSON en ne renvoyant pas l'entité directement.
     */
    public VehiculeResponse toResponse(Vehicule vehicule) {
        VehiculeResponse r = new VehiculeResponse();

        // Champs communs
        r.setId(vehicule.getId());
        r.setType(vehicule.getType());
        r.setMarque(vehicule.getMarque());
        r.setModele(vehicule.getModele());
        r.setImmatriculation(vehicule.getImmatriculation());
        r.setAnnee(vehicule.getAnnee());
        r.setPrixParJour(vehicule.getPrixParJour());
        r.setCaution(vehicule.getCaution());
        r.setStatut(vehicule.getStatut());
        r.setImage(vehicule.getImage());
        r.setTypeCarburant(vehicule.getTypeCarburant());
        r.setTypeBoite(vehicule.getTypeBoiteVitesse());

        // Champs spécifiques Voiture
        if (vehicule instanceof Voiture v) {
            r.setNbPortes(v.getNbPortes());
            r.setNbPlaces(v.getNbPlaces());
            r.setCategorie(v.getCategorie());
        }

        // Champs spécifiques Camion
        if (vehicule instanceof Camion c) {
            r.setTonnage(c.getTonnage());
            r.setVolume(c.getVolume());
            r.setLongueur(c.getLongueur());
            r.setElevator(c.getElevator());
        }

        // Options (M:N) → List<OptionResponse>
        r.setOptions(
                vehicule.getOptions().stream()
                        .map(o -> new VehiculeResponse.OptionResponse(o.getId(), o.getNom()))
                        .collect(Collectors.toList())
        );

        return r;
    }
}