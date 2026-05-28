package ru.village.domain;

import jakarta.persistence.*;
import lombok.*;

/** Здание (номер дома). */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bldng", schema = "village")
public class Bldng {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String number;

    private String dscrptn;
}
