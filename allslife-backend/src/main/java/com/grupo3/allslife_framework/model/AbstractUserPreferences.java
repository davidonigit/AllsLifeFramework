package com.grupo3.allslife_framework.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // A mesma estratégia da Rotina
@DiscriminatorColumn(name = "PREFERENCE_TYPE") // Coluna para saber o tipo de preferência
@Data
public abstract class AbstractUserPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}