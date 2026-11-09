package dk.via.pro3.slaughterhouse.repository;

import dk.via.pro3.slaughterhouse.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repository for Product. Spring Data JPA generates the implementation at startup,
// including the query below, which it builds from the method name.
public interface SlaughterhouseRepository extends JpaRepository<Product, Integer> {

    // Query 2 from queries.sql: all products an animal has been involved in.
    // Read the name as: find DISTINCT products
    //   BY trays -> parts -> animal -> registrationNumber = the parameter,
    //   ORDER BY productId.
    // The underscores mark each step through a relationship.
    List<Product> findDistinctByTrays_Parts_Animal_RegistrationNumberOrderByProductId(String registrationNumber);
}
