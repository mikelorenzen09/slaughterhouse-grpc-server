package dk.via.pro3.slaughterhouse.grpc.service;

import dk.via.pro3.slaughterhouse.model.Animal;
import dk.via.pro3.slaughterhouse.model.Product;
import dk.via.pro3.slaughterhouse.model.ProductRecord;
import dk.via.pro3.slaughterhouse.repository.AnimalRepository;
import dk.via.pro3.slaughterhouse.repository.SlaughterhouseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SlaughterhouseService {

  private final AnimalRepository animalRepository;
  private final SlaughterhouseRepository productRepository;

  public SlaughterhouseService(AnimalRepository animalRepository, SlaughterhouseRepository productRepository) {
    this.animalRepository = animalRepository;
    this.productRepository = productRepository;
  }

  public List<String> getAnimalsByProduct(int productId) {
    // The repository returns whole Animal entities; the gRPC layer only needs their registration numbers
    return animalRepository.findDistinctByParts_Tray_Products_ProductIdOrderByRegistrationNumber(productId)
        .stream()
        .map(Animal::getRegistrationNumber)
        .toList();
  }

  public List<ProductRecord> getProductsByAnimal(String registrationNumber) {
    // Convert entities to plain records, so the gRPC layer never sees JPA entities
    return productRepository.findDistinctByTrays_Parts_Animal_RegistrationNumberOrderByProductId(registrationNumber)
        .stream()
        .map(SlaughterhouseService::toRecord)
        .toList();
  }

  private static ProductRecord toRecord(Product product) {
    return new ProductRecord(
        product.getProductId(),
        product.getName(),
        product.getProductType(),
        product.getPackagedDate());
  }
}
