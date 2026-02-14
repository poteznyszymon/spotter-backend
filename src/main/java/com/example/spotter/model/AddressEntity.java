package com.example.spotter.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "addresses")
public class AddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String city;

    private String state;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String houseNumber;

    private String apartmentNumber;

    @Column(nullable = false)
    private String postalCode;

    private Double latitude;
    private Double longitude;

}
