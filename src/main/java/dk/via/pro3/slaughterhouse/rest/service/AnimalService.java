package dk.via.pro3.slaughterhouse.rest.service;

import dk.via.pro3.slaughterhouse.rest.dto.AnimalDTO;

import java.time.LocalDate;
import java.util.List;

public interface AnimalService {
    AnimalDTO registerAnimal(AnimalDTO animal);

    AnimalDTO getAnimal(String registrationNumber);

    List<AnimalDTO> getAnimalsByDate(LocalDate date);

    List<AnimalDTO> getAnimalsByOrigin(String origin);
}
