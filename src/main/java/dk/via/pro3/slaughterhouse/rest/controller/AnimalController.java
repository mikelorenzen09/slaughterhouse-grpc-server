package dk.via.pro3.slaughterhouse.rest.controller;

import dk.via.pro3.slaughterhouse.rest.dto.AnimalDTO;
import dk.via.pro3.slaughterhouse.rest.service.AnimalService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/animals")
public class AnimalController {
    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @PostMapping
    public ResponseEntity<Void> registerAnimal(@RequestBody AnimalDTO animal) {
        AnimalDTO registered = animalService.registerAnimal(animal);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{registrationNumber}")
                .buildAndExpand(registered.registrationNumber()).encode().toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{registrationNumber}")
    public ResponseEntity<AnimalDTO> getAnimal(@PathVariable String registrationNumber) {
        AnimalDTO animal = animalService.getAnimal(registrationNumber);
        return animal == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(animal);
    }

    @GetMapping("/date/{date}")
    public List<AnimalDTO> getAnimalsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return animalService.getAnimalsByDate(date);
    }

    @GetMapping("/origin/{origin}")
    public List<AnimalDTO> getAnimalsByOrigin(@PathVariable String origin) {
        return animalService.getAnimalsByOrigin(origin);
    }
}
