package dk.via.pro3.slaughterhouse.rest;

import dk.via.pro3.slaughterhouse.model.Animal;
import dk.via.pro3.slaughterhouse.repository.AnimalRepository;
import dk.via.pro3.slaughterhouse.rest.dto.AnimalDTO;
import dk.via.pro3.slaughterhouse.rest.service.AnimalServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AnimalServiceTests {
    private final AnimalRepository repository = mock(AnimalRepository.class);
    private final AnimalServiceImpl service = new AnimalServiceImpl(repository);

    @Test
    void registrationPreservesAllFieldsAndDecimalWeight() {
        AnimalDTO request = new AnimalDTO("DK-123", "Pig", new BigDecimal("120.125"),
                LocalDate.of(2026, 11, 5), "Farm A");
        when(repository.save(any(Animal.class))).thenAnswer(call -> call.getArgument(0));

        assertThat(service.registerAnimal(request)).isEqualTo(request);
        verify(repository).save(argThat(animal -> animal.getParts().isEmpty()
                && animal.getRegistrationNumber().equals("DK-123")
                && animal.getOrigin().equals("Farm A")));
    }

    @Test
    void duplicateRegistrationIsRejectedBeforeSaving() {
        when(repository.existsById("DK-123")).thenReturn(true);
        AnimalDTO duplicate = new AnimalDTO("DK-123", "Pig", BigDecimal.TEN,
                LocalDate.of(2026, 11, 5), "Other farm");

        assertStatus(duplicate, HttpStatus.CONFLICT);
        verify(repository, never()).save(any());
    }

    @Test
    void invalidRegistrationsNeverReachTheRepository() {
        LocalDate date = LocalDate.of(2026, 11, 5);
        for (AnimalDTO invalid : List.of(
                new AnimalDTO(null, null, BigDecimal.TEN, date, "Farm A"),
                new AnimalDTO(" ", null, BigDecimal.TEN, date, "Farm A"),
                new AnimalDTO("DK-123", null, null, date, "Farm A"),
                new AnimalDTO("DK-123", null, BigDecimal.ZERO, date, "Farm A"),
                new AnimalDTO("DK-123", null, BigDecimal.ONE.negate(), date, "Farm A"),
                new AnimalDTO("DK-123", null, BigDecimal.TEN, null, "Farm A"),
                new AnimalDTO("DK-123", null, BigDecimal.TEN, date, null),
                new AnimalDTO("DK-123", null, BigDecimal.TEN, date, " "),
                new AnimalDTO("X".repeat(256), null, BigDecimal.TEN, date, "Farm A"),
                new AnimalDTO("DK-123", null, BigDecimal.TEN, date, "X".repeat(256)))) {
            assertStatus(invalid, HttpStatus.BAD_REQUEST);
        }
        verifyNoInteractions(repository);
    }

    private void assertStatus(AnimalDTO request, HttpStatus status) {
        assertThatThrownBy(() -> service.registerAnimal(request))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        error -> assertThat(error.getStatusCode()).isEqualTo(status));
    }
}
