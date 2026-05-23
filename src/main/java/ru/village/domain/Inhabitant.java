package ru.village.domain;

import jakarta.persistence.*;
import lombok.*;

/** Житель домохозяйства. is_master — флаг "контактного" жителя дома. */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inhabitant", schema = "village")
public class Inhabitant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String phone;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hh_id")
    private Household household;

    @Column(name = "is_master", nullable = false)
    private boolean master;
}
