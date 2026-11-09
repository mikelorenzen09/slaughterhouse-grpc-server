package dk.via.pro3.slaughterhouse.rest;

import dk.via.pro3.slaughterhouse.generated.*;
import dk.via.pro3.slaughterhouse.repository.AnimalRepository;
import dk.via.pro3.slaughterhouse.rest.dto.AnimalDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.grpc.test.autoconfigure.AutoConfigureTestGrpcTransport;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.grpc.client.ImportGrpcClients;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

// Set SLAUGHTERHOUSE_TEST_DATABASE_URL to a separate database initialized with database/schema.sql.
@EnabledIfEnvironmentVariable(named = "SLAUGHTERHOUSE_TEST_DATABASE_URL", matches = "jdbc:postgresql:.*")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.grpc.server.port=0", "spring.jpa.show-sql=false"})
@AutoConfigureTestGrpcTransport
@ImportGrpcClients(types = SlaughterhouseServiceGrpc.SlaughterhouseServiceBlockingStub.class)
class AnimalRegistrationIntegrationTests {
    @Autowired private DataSource dataSource;
    @Autowired private AnimalRepository repository;
    @Autowired private SlaughterhouseServiceGrpc.SlaughterhouseServiceBlockingStub stub;
    @Value("${local.server.port}") private int port;

    private final String prefix = "TEST-" + UUID.randomUUID();
    private int trayId;
    private int productId;

    @DynamicPropertySource
    static void testDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> System.getenv("SLAUGHTERHOUSE_TEST_DATABASE_URL"));
    }

    @Test
    void registrationLookupsAndExistingGrpcQueriesWorkTogether() throws Exception {
        RestClient client = RestClient.create("http://localhost:" + port);
        LocalDate date = LocalDate.of(2026, 11, 5);
        AnimalDTO first = new AnimalDTO(prefix + "-1", "Pig", new BigDecimal("120.125"), date, prefix + " Farm A");
        AnimalDTO second = new AnimalDTO(prefix + "-2", "Pig", BigDecimal.TEN, date, prefix + " Farm B");
        AnimalDTO third = new AnimalDTO(prefix + "-3", null, BigDecimal.ONE, date.plusDays(1), first.origin());

        for (AnimalDTO animal : new AnimalDTO[]{first, second, third}) {
            var response = client.post().uri("/api/v1/animals").body(animal).retrieve().toBodilessEntity();
            assertThat(response.getStatusCode().value()).isEqualTo(201);
            AnimalDTO read = client.get().uri(response.getHeaders().getLocation()).retrieve().body(AnimalDTO.class);
            assertThat(read).isEqualTo(animal);
        }
        assertThat(repository.findById(first.registrationNumber()).orElseThrow().getWeightKg())
                .isEqualByComparingTo(first.weightKg());

        AnimalDTO[] arrivals = client.get().uri("/api/v1/animals/date/{date}", date)
                .retrieve().body(AnimalDTO[].class);
        assertThat(arrivals).contains(first, second).doesNotContain(third);
        assertThat(arrivals).allSatisfy(animal -> assertThat(animal.arrivalDate()).isEqualTo(date));
        AnimalDTO[] origins = client.get().uri("/api/v1/animals/origin/{origin}", first.origin())
                .retrieve().body(AnimalDTO[].class);
        assertThat(origins).containsExactlyInAnyOrder(first, third);
        assertThat(client.get().uri("/api/v1/animals/origin/{origin}", prefix + " missing farm")
                .retrieve().body(AnimalDTO[].class)).isEmpty();

        assertThat(client.post().uri("/api/v1/animals").body(first)
                .<Integer>exchange((request, response) -> response.getStatusCode().value())).isEqualTo(409);
        assertThat(client.get().uri("/api/v1/animals/{id}", prefix + "-missing")
                .<Integer>exchange((request, response) -> response.getStatusCode().value())).isEqualTo(404);
        assertThat(client.get().uri("/api/v1/animals/date/not-a-date")
                .<Integer>exchange((request, response) -> response.getStatusCode().value())).isEqualTo(400);
        assertThat(client.post().uri("/api/v1/animals").contentType(MediaType.APPLICATION_JSON)
                .body("{\"registrationNumber\":\"invalid\",\"weightKg\":0}")
                .<Integer>exchange((request, response) -> response.getStatusCode().value())).isEqualTo(400);

        // Real PostgreSQL fixtures: two animals share a tray; repeated parts must not duplicate results.
        try (Connection connection = dataSource.getConnection(); Statement sql = connection.createStatement()) {
            try (var rows = sql.executeQuery("INSERT INTO tray(part_type,max_capacity_kg) VALUES ('LEG',100) RETURNING tray_id")) {
                rows.next(); trayId = rows.getInt(1);
            }
            try (var rows = sql.executeQuery("INSERT INTO product(name,product_type,packaged_date) VALUES ('Test package','SAME_TYPE','2026-11-05') RETURNING product_id")) {
                rows.next(); productId = rows.getInt(1);
            }
            sql.executeUpdate("INSERT INTO product_tray VALUES (" + productId + "," + trayId + ")");
            for (AnimalDTO animal : new AnimalDTO[]{first, first, second}) {
                try (var part = connection.prepareStatement("INSERT INTO part(animal_registration_number,part_type,weight_kg,tray_id) VALUES (?,'LEG',1,?)")) {
                    part.setString(1, animal.registrationNumber()); part.setInt(2, trayId); part.executeUpdate();
                }
            }
        }
        var animals = stub.withDeadlineAfter(5, TimeUnit.SECONDS)
                .getAnimalsByProduct(GetAnimalsByProductRequest.newBuilder().setProductId(productId).build());
        assertThat(animals.getRegistrationNumbersList()).containsExactlyInAnyOrder(first.registrationNumber(), second.registrationNumber());
        var products = stub.withDeadlineAfter(5, TimeUnit.SECONDS)
                .getProductsByAnimal(GetProductsByAnimalRequest.newBuilder().setRegistrationNumber(first.registrationNumber()).build());
        assertThat(products.getProductsList()).hasSize(1);
        assertThat(products.getProducts(0).getProductId()).isEqualTo(productId);
        // Even an animal with parts is returned as a flat DTO, without traversing the JPA graph.
        assertThat(client.get().uri("/api/v1/animals/{id}", first.registrationNumber())
                .retrieve().body(String.class)).doesNotContain("parts", "trays", "products");
    }

    @AfterEach
    void removeOnlyThisTestsFixtures() throws Exception {
        try (Connection connection = dataSource.getConnection(); Statement sql = connection.createStatement()) {
            sql.executeUpdate("DELETE FROM product_tray WHERE product_id=" + productId);
            sql.executeUpdate("DELETE FROM part WHERE tray_id=" + trayId);
            sql.executeUpdate("DELETE FROM product WHERE product_id=" + productId);
            sql.executeUpdate("DELETE FROM tray WHERE tray_id=" + trayId);
        }
        for (int i = 1; i <= 3; i++) {
            repository.deleteById(prefix + "-" + i);
        }
    }
}
