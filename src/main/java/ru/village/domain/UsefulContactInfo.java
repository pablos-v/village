package ru.village.domain;

import jakarta.persistence.*;
import lombok.*;

/** Полезный контакт (врач, квартальная) для жителей. */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "useful_contact_info", schema = "village")
public class UsefulContactInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(name = "contact_info", nullable = false, columnDefinition = "text")
    private String contactInfo;

    @Column(length = 500)
    private String comment;
}
