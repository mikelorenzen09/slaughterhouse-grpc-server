package dk.via.pro3.libraryservice.service;

import dk.via.pro3.libraryservice.dto.BorrowerDTO;
import dk.via.pro3.libraryservice.dto.ResponseDTO;
import dk.via.pro3.libraryservice.dto.VinylDTO;
import dk.via.pro3.libraryservice.model.Borrower;
import dk.via.pro3.libraryservice.repository.BorrowerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LibraryServiceImpl implements LibraryService
{
  private final BorrowerRepository borrowerRepository;
  private final RestClient restClient;

  public LibraryServiceImpl(BorrowerRepository borrowerRepository, RestClient restClient)
  {
    this.borrowerRepository = borrowerRepository;
    this.restClient = restClient;
  }

  @Override
  public ResponseDTO getBorrower(Long borrowerId)
  {
    Borrower b = borrowerRepository.findById(borrowerId);
    if (b == null)
    {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Borrower not found");
    }
    BorrowerDTO borrowerDTO = mapToBorrower(b);
    VinylDTO vinylDTO = restClient.get().uri("/api/v1/vinyls/{id}", b.getVinylId()).retrieve().body(VinylDTO.class);
    return new ResponseDTO(vinylDTO, borrowerDTO);
  }

  private BorrowerDTO mapToBorrower(Borrower b)
  {
    return new BorrowerDTO(b.getId(), b.getFirstName(), b.getLastName(), b.getEmail());
  }
}
