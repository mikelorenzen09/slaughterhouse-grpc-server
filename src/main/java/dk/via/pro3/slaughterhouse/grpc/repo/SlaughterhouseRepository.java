package dk.via.pro3.slaughterhouse.grpc.repo;

import dk.via.pro3.slaughterhouse.grpc.model.ProductRecord;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.List;

// Reads from the database. JdbcClient is created by Spring from application.properties.
@Service
public class SlaughterhouseRepository {

  private final JdbcClient jdbc;

  public SlaughterhouseRepository(JdbcClient jdbc) {
    this.jdbc = jdbc;
  }

  // Query 1 from queries.sql
  public List<String> findRegistrationNumbersByProduct(int productId) {
    return jdbc.sql("""
                        SELECT DISTINCT p.animal_registration_number
                        FROM product_tray pt
                                 JOIN part p ON p.tray_id = pt.tray_id
                        WHERE pt.product_id = :productId
                        ORDER BY p.animal_registration_number
                        """)
        .param("productId", productId)
        .query(String.class)
        .list();
  }

  // Query 2 from queries.sql
  public List<ProductRecord> findProductsByAnimal(String registrationNumber) {
    return jdbc.sql("""
                        SELECT DISTINCT pr.product_id, pr.name, pr.product_type, pr.packaged_date
                        FROM part p
                                 JOIN product_tray pt ON pt.tray_id = p.tray_id
                                 JOIN product pr ON pr.product_id = pt.product_id
                        WHERE p.animal_registration_number = :regNo
                        ORDER BY pr.product_id
                        """)
        .param("regNo", registrationNumber)
        .query(ProductRecord.class)
        .list();
  }
}