package dk.via.pro3.slaughterhouse.rest.service;

import dk.via.pro3.slaughterhouse.model.Animal;
import dk.via.pro3.slaughterhouse.repository.AnimalRepository;
import dk.via.pro3.slaughterhouse.rest.dto.AnimalDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class AnimalServiceImpl implements AnimalService {
    private final AnimalRepository repository;

    public AnimalServiceImpl(AnimalRepository repository) {
        this.repository = repository;
    }

    @Override
    public AnimalDTO registerAnimal(AnimalDTO animal) {
        if (animal.registrationNumber() == null || animal.registrationNumber().isBlank()
                || animal.registrationNumber().length() > 255
                || animal.arrivalDate() == null
                || animal.weightKg() == null || animal.weightKg().signum() <= 0
                || animal.origin() == null || animal.origin().isBlank()
                || animal.origin().length() > 255
                || (animal.species() != null && animal.species().length() > 255)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Registration number, date, positive weight and origin are required");
        }
        if (repository.existsById(animal.registrationNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Animal is already registered");
        }

        Animal entity = new Animal();
        entity.setRegistrationNumber(animal.registrationNumber());
        entity.setSpecies(animal.species());
        entity.setWeightKg(animal.weightKg());
        entity.setArrivalDate(animal.arrivalDate());
        entity.setOrigin(animal.origin());
        return toDTO(repository.save(entity));
    }

    @Override
    public AnimalDTO getAnimal(String registrationNumber) {
        return repository.findById(registrationNumber).map(AnimalServiceImpl::toDTO).orElse(null);
    }

    @Override
    public List<AnimalDTO> getAnimalsByDate(LocalDate date) {
        return repository.findByArrivalDate(date).stream().map(AnimalServiceImpl::toDTO).toList();
    }

    @Override
    public List<AnimalDTO> getAnimalsByOrigin(String origin) {
        return repository.findByOrigin(origin).stream().map(AnimalServiceImpl::toDTO).toList();
    }

    private static AnimalDTO toDTO(Animal animal) {
        return new AnimalDTO(animal.getRegistrationNumber(), animal.getSpecies(), animal.getWeightKg(),
                animal.getArrivalDate(), animal.getOrigin());
    }
}
