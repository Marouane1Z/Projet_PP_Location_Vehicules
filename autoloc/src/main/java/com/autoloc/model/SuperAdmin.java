package com.autoloc.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "super_admin")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuperAdmin extends Admin {

    @OneToMany
    @JoinColumn(name = "super_admin_id")
    private List<Admin> admins;
}