package com.autoloc.model;
import com.autoloc.enums.*;
import jakarta.persistence.*;
import lombok.*;


//@Entity              → "je suis une table"
//@Table               → "mon nom en base est..."
//@Inheritance         → "mes enfants ont leur propre table"
//@Getter / @Setter    → "génère mes getters/setters"
//@NoArgsConstructor   → "génère User() vide"
//@AllArgsConstructor  → "génère User(tous les champs)"


@Entity
@Table(name = "utilisateur")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column(nullable = false)
    private String firstname;
    @Column(nullable = false)
    private String lastname;


    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private userRole role ;

    private String address;

    private Boolean actif = true;



}
