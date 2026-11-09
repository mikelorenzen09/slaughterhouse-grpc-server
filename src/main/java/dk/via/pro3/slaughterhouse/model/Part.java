package dk.via.pro3.slaughterhouse.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

// Station 2: a part cut from an animal and put in a tray.
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // SERIAL column: the database picks the id
    private Integer partId;

    // Default column name is animal_registration_number, which matches the table
    @ManyToOne(fetch = FetchType.LAZY)
    private Animal animal;

    private String partType;
    private BigDecimal weightKg;

    // The default would be "tray_tray_id", so the real column name is needed here
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tray_id")
    private Tray tray;
}
