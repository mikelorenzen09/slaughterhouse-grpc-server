package dk.via.pro3.slaughterhouse.repository;

import dk.via.pro3.slaughterhouse.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

// Repository for Animal. Spring Data JPA generates the implementation at startup.
public interface AnimalRepository extends JpaRepository<Animal, String> {

    List<Animal> findByArrivalDate(LocalDate arrivalDate);

    List<Animal> findByOrigin(String origin);

    // Query 1 from queries.sql: all animals involved in a product.
    // Read the name as: find DISTINCT animals
    //   BY parts -> tray -> products -> productId = the parameter,
    //   ORDER BY registrationNumber.
    List<Animal> findDistinctByParts_Tray_Products_ProductIdOrderByRegistrationNumber(int productId);
}
