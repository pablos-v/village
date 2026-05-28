package ru.village.domain;

import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

/** Read-only VIEW текущего остатка кассы. */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Immutable
@Table(name = "balance_view", schema = "village")
public class BalanceView {

    @Id
    @Column(nullable = false)
    private BigDecimal amount;
}
