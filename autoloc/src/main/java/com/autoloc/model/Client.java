package com.autoloc.model;

import com.autoloc.enums.statutReservation;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "client")
@PrimaryKeyJoinColumn(name = "id")
public class Client extends User {

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "permis_numero", referencedColumnName = "numero")
    private PermisConduire permisConduire;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_reservation")
    private statutReservation statutReservation;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Reservation> reservations;
}
