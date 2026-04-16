package com.autoloc.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;


@Entity
@Table(name = "admin")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Admin extends User {
    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    private List<OrdreMaintenance> ordreMaintenances;
}