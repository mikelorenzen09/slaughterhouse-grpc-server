package dk.via.pro3.restapiexample.service;

import dk.via.pro3.restapiexample.model.Vinyl;
import dk.via.pro3.restapiexample.repository.VinylList;

public interface VinylService
{
  VinylList getVinylList();

  VinylList addVinyl(Vinyl vinyl);
  Vinyl getVinylByID(int id);
}
