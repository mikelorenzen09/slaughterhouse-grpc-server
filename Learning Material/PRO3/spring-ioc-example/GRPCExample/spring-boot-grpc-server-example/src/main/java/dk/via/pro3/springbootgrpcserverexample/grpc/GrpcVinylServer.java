package dk.via.pro3.springbootgrpcserverexample.grpc;

import dk.via.pro3.springbootgrpcserverexample.model.VinylRecord;
import dk.via.pro3.springbootgrpcserverexample.service.VinylService;
import dk.via.pro3.springbootgrpcexample.generated.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class GrpcVinylServer extends VinylServiceGrpc.VinylServiceImplBase {
    private final VinylService vinylService;

    public GrpcVinylServer(VinylService vinylService) {
        this.vinylService = vinylService;
    }

    @Override
    public void addVinyl(AddVinylRequest request, StreamObserver<VinylResponse> observer) {
        if (request.getName().isBlank()) {
            observer.onError(Status.INVALID_ARGUMENT.withDescription("Vinyl name must not be blank.")
                    .asRuntimeException());
            return;
        }
        respond(vinylService.addVinyl(request.getName()), observer);
    }

    @Override
    public void getVinyl(GetVinylRequest request, StreamObserver<VinylResponse> observer) {
        if (request.getId().isBlank()) {
            observer.onError(Status.INVALID_ARGUMENT.withDescription("Vinyl ID must not be blank.")
                    .asRuntimeException());
            return;
        }
        VinylRecord vinyl = vinylService.getVinylById(request.getId());
        if (vinyl == null) {
            observer.onError(Status.NOT_FOUND.withDescription("No vinyl found for ID " + request.getId())
                    .asRuntimeException());
            return;
        }
        respond(vinyl, observer);
    }

    private static void respond(VinylRecord record, StreamObserver<VinylResponse> observer) {
        Vinyl vinyl = Vinyl.newBuilder().setId(record.id()).setName(record.name()).build();
        observer.onNext(VinylResponse.newBuilder().setVinyl(vinyl).build());
        observer.onCompleted();
    }
}
