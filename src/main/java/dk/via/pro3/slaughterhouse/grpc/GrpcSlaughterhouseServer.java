package dk.via.pro3.slaughterhouse.grpc;

import dk.via.pro3.slaughterhouse.generated.*;
import dk.via.pro3.slaughterhouse.model.ProductRecord;
import dk.via.pro3.slaughterhouse.grpc.service.SlaughterhouseService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class GrpcSlaughterhouseServer extends SlaughterhouseServiceGrpc.SlaughterhouseServiceImplBase {
    private static final Logger LOG = LoggerFactory.getLogger(GrpcSlaughterhouseServer.class.getName());

    private final SlaughterhouseService slaughterhouseService;

    public GrpcSlaughterhouseServer(SlaughterhouseService slaughterhouseService)
    {
        this.slaughterhouseService = slaughterhouseService;
    }

    @Override
    public void getAnimalsByProduct(GetAnimalsByProductRequest request,
        StreamObserver<GetAnimalsByProductResponse> responseObserver) {
        var regNumbers = slaughterhouseService.getAnimalsByProduct(request.getProductId());
        if (regNumbers.isEmpty()) {
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription("Product with id [%d] not found".formatted(request.getProductId()))
                    .asRuntimeException());
            return;
        }

        var response = GetAnimalsByProductResponse.newBuilder()
            .addAllRegistrationNumbers(regNumbers)
            .build();
        responseObserver.onNext(response);
        LOG.info("Animals {} retrieved for product [{}]\n", regNumbers, request.getProductId());
        responseObserver.onCompleted();
    }

    @Override
    public void getProductsByAnimal(GetProductsByAnimalRequest request,
        StreamObserver<GetProductsByAnimalResponse> responseObserver) {
        var products = slaughterhouseService.getProductsByAnimal(request.getRegistrationNumber());
        var response = GetProductsByAnimalResponse.newBuilder();
        for (var product : products) {
            response.addProducts(ProductMapper.toProto(product));
        }
        responseObserver.onNext(response.build());
        LOG.info("{} product(s) retrieved for animal [{}]\n", products.size(), request.getRegistrationNumber());
        responseObserver.onCompleted();
    }

    private static class ProductMapper
    {
        static Product toProto(ProductRecord productRecord)
        {
            return Product.newBuilder()
                .setProductId(productRecord.productId())
                .setName(productRecord.name())
                .setProductType(productRecord.productType())
                .setPackagedDate(productRecord.packagedDate().toString())
                .build();
        }
    }
}