package com.belvinard.inventory_management.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
@Embeddable
public class Address implements Serializable {

    @Size(min = 5, max = 100, message = "Address line 1 must be between 5 and 100 characters")
    private String address1;

    @Size(max = 100, message = "Address line 2 must not exceed 100 characters")
    private String address2;

    @Size(min = 4, max = 50, message = "City must be between 4 and 50 characters")
    private String city;

    @Size(min = 3, max = 10, message = "Postal code must be between 3 and 10 characters")
    private String postalCode;

    @Size(min = 2, max = 50, message = "Country must be between 2 and 50 characters")
    private String country;
}