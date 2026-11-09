package dk.via.pro3.libraryservice.controller;

import dk.via.pro3.libraryservice.dto.ResponseDTO;
import dk.via.pro3.libraryservice.service.LibraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/borrowers")
public class LibraryController
{
  private LibraryService libraryService;

  public LibraryController(LibraryService libraryService)
  {
    this.libraryService = libraryService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseDTO> getBorrower (@PathVariable Long id)
  {
    ResponseDTO response = libraryService.getBorrower(id);
    return ResponseEntity.ok(response);
  }

}
