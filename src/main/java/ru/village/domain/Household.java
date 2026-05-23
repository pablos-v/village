package ru.village.domain;

import jakarta.persistence.*;
import lombok.*;

/** Домохозяйство — конкретный дом по адресу. */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "household", schema = "village")
public class Household {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "addrss_id")
    private Address address;
}
