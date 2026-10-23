package dk.via.pro3.slaughterhouse.grpc.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Station 1: an animal arriving at the slaughterhouse.
// Table and column names come from the class and field names: registrationNumber -> registration_number.
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Animal {

    @Id
    private String registrationNumber;

    private String species;
    private BigDecimal weightKg;
    private LocalDate arrivalDate;

    @OneToMany(mappedBy = "animal")
    private List<Part> parts = new ArrayList<>();
}
