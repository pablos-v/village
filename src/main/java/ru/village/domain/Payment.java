package ru.village.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.*;

/** Поступление денег от домохозяйства за конкретное событие. */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment", schema = "village")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hh_id")
    private Household household;

    @Column(nullable = false)
    private LocalDate paydate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evnt_id")
    private Event event;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
}
