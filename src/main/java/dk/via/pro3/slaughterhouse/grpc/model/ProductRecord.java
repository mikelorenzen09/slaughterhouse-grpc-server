package dk.via.pro3.slaughterhouse.grpc.model;

import java.time.LocalDate;

public record ProductRecord(int productId, String name, String productType, LocalDate packagedDate) {
}