package dk.via.pro3.restapiexample.service;

import dk.via.pro3.restapiexample.model.Vinyl;
import dk.via.pro3.restapiexample.repository.VinylDAO;
import dk.via.pro3.restapiexample.repository.VinylList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VinylServiceImpl implements VinylService
{

  @Autowired
  public VinylServiceImpl(VinylDAO vinylDAO)
  {
    this.vinylDAO = vinylDAO;
  }

  private VinylDAO vinylDAO;
  @Override public VinylList getVinylList()
  {
    return vinylDAO.getAllVinyls();
  }
  @Override
  public VinylList addVinyl(Vinyl vinyl)
  {
    vinylDAO.addVinyl(vinyl);
    return null;
  }

  @Override
  public Vinyl getVinylByID(int id)
  {
    return vinylDAO.getVinylById(id);
  }

}
