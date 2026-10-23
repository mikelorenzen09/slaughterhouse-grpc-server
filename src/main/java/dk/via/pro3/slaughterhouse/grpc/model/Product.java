package dk.via.pro3.slaughterhouse.grpc.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

// Station 3: a packed product (not the generated gRPC message with the same name).
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // SERIAL column: the database picks the id
    private Integer productId;

    private String name;
    private String productType; // SAME_TYPE or HALF_ANIMAL
    private LocalDate packagedDate;

    // The defaults would be "products_product_id" and "trays_tray_id", so the real columns are needed here
    @ManyToMany
    @JoinTable(
            name = "product_tray",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tray_id"))
    private Set<Tray> trays = new HashSet<>();
}
