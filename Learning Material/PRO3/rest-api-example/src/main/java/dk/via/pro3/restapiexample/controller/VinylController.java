package dk.via.pro3.restapiexample.controller;

import dk.via.pro3.restapiexample.model.Vinyl;
import dk.via.pro3.restapiexample.service.VinylService;
import dk.via.pro3.restapiexample.repository.VinylList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("api/v1/vinyls")
public class VinylController
{
  private VinylService vinylService;

  @Autowired
  public VinylController(VinylService vinylService)
  {
    this.vinylService=vinylService;
  }
  @GetMapping("/")
  public VinylList getVinylList()
  {
    return vinylService.getVinylList();
  }

  @GetMapping({"/{id}", "/{id}/"})
  public ResponseEntity<Vinyl> getVinyl(@PathVariable int id)
  {
    Vinyl vinyl = vinylService.getVinylByID(id);
    return vinyl == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(vinyl);
  }

  @PostMapping
  public ResponseEntity<Object> addVinyl(@RequestBody Vinyl vinyl)
  {
    vinylService.addVinyl(vinyl);

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(vinyl.getId())
        .toUri();

    return ResponseEntity.created(location).build();
  }

  @GetMapping
  public Vinyl getVinylByID(@PathVariable int id)
  {
    vinylService.getVinylByID(id);
    return null;
  }

}
