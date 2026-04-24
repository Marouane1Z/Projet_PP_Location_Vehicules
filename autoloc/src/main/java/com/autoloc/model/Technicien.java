package com.autoloc.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "technicien")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Technicien extends User{

    @OneToMany
    @JoinColumn(name = "id_ordre_maintenance")
    private List<OrdreMaintenance> ordreMaintenances;

    @Column(name = "disponible")
    private boolean disponible ;

    @Column(name = "specialite")
    private String specialite;


}
