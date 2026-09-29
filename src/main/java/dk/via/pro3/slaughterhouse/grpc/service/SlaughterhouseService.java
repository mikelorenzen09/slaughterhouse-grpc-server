package dk.via.pro3.slaughterhouse.grpc.service;

import dk.via.pro3.slaughterhouse.grpc.model.ProductRecord;
import dk.via.pro3.slaughterhouse.grpc.repo.SlaughterhouseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SlaughterhouseService {

  private final SlaughterhouseRepository repository;

  public SlaughterhouseService(SlaughterhouseRepository repository) {
    this.repository = repository;
  }

  public List<String> getAnimalsByProduct(int productId) {
    return repository.findRegistrationNumbersByProduct(productId);
  }

  public List<ProductRecord> getProductsByAnimal(String registrationNumber) {
    return repository.findProductsByAnimal(registrationNumber);
  }
}