package dk.via.pro3.springbootgrpcclientexample.service;

import dk.via.pro3.springbootgrpcexample.generated.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class VinylClientService {
    private final VinylServiceGrpc.VinylServiceBlockingStub stub;

    public VinylClientService(VinylServiceGrpc.VinylServiceBlockingStub stub) {
        this.stub = stub;
    }

    public Vinyl addVinyl(String name) {
        return stub.withDeadlineAfter(5, TimeUnit.SECONDS)
                .addVinyl(AddVinylRequest.newBuilder().setName(name).build()).getVinyl();
    }

    public Vinyl getVinyl(String id) {
        return stub.withDeadlineAfter(5, TimeUnit.SECONDS)
                .getVinyl(GetVinylRequest.newBuilder().setId(id).build()).getVinyl();
    }
}
