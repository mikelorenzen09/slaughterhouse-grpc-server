package dk.via.pro3.libraryservice.service;

import dk.via.pro3.libraryservice.dto.ResponseDTO;

public interface LibraryService
{
  ResponseDTO getBorrower(Long borrowerId);

}
