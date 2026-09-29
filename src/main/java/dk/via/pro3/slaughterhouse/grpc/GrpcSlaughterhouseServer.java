package dk.via.pro3.slaughterhouse.grpc;

import dk.via.pro3.slaughterhouse.generated.*;
import dk.via.pro3.slaughterhouse.grpc.model.ProductRecord;
import dk.via.pro3.slaughterhouse.grpc.service.SlaughterhouseService;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrpcSlaughterhouseServer extends SlaughterhouseServiceGrpc.SlaughterhouseServiceImplBase
{

    private final SlaughterhouseService service;

    public GrpcSlaughterhouseServer(SlaughterhouseService service)
    {
        this.service = service;
    }

    @Override public void getAnimalsByProduct(
        GetAnimalsByProductRequest request,
        StreamObserver<GetAnimalsByProductResponse> responseObserver)
    {
        List<String> regNumbers = service.getAnimalsByProduct(
            request.getProductId());

        GetAnimalsByProductResponse response = GetAnimalsByProductResponse.newBuilder()
            .addAllRegistrationNumbers(regNumbers).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override public void getProductsByAnimal(
        GetProductsByAnimalRequest request,
        StreamObserver<GetProductsByAnimalResponse> responseObserver)
    {
        List<ProductRecord> products = service.getProductsByAnimal(
            request.getRegistrationNumber());

        GetProductsByAnimalResponse.Builder response = GetProductsByAnimalResponse.newBuilder();
        for (ProductRecord p : products)
        {
            response.addProducts(
                Product.newBuilder().setProductId(p.productId())
                    .setName(p.name()).setProductType(p.productType())
                    .setPackagedDate(p.packagedDate().toString()).build());
        }
    }
}