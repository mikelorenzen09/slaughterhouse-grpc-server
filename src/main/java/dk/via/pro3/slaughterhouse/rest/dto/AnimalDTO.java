package dk.via.pro3.slaughterhouse.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AnimalDTO(String registrationNumber, String species, BigDecimal weightKg,
                        LocalDate arrivalDate, String origin) {
}
