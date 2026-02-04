package com.outsera.goldenraspberry.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity representing a movie from the Golden Raspberry Awards.
 * <p>
 * Maps to the movies table and stores data loaded from the CSV file.
 * Each record represents a movie that was either a nominee or winner
 * in the "Worst Film" category.
 * </p>
 */
@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "release_year", nullable = false)
    private Integer year;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "studios", length = 500)
    private String studios;

    @Column(name = "producers", nullable = false, length = 500)
    private String producers;

    @Column(name = "winner")
    private Boolean winner;
}
