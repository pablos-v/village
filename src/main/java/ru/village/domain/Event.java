package ru.village.domain;

import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.*;

/** Платёжный период / событие сбора (например, "2026 март"). */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events", schema = "village")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(precision = 10, scale = 2)
    private BigDecimal cost;
}
