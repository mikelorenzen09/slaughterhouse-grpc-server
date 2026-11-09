package dk.via.pro3.slaughterhouse.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Station 2: a tray holding parts of one type.
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Tray {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // SERIAL column: the database picks the id
    private Integer trayId;

    private String partType;
    private BigDecimal maxCapacityKg;

    @OneToMany(mappedBy = "tray")
    private List<Part> parts = new ArrayList<>();

    @ManyToMany(mappedBy = "trays")
    private Set<Product> products = new HashSet<>();
}
