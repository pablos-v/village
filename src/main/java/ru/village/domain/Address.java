package ru.village.domain;

import jakarta.persistence.*;
import lombok.*;

/** Адрес — связь улицы и здания. */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "address", schema = "village")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "street_id")
    private Street street;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bldng_id")
    private Bldng bldng;
}
