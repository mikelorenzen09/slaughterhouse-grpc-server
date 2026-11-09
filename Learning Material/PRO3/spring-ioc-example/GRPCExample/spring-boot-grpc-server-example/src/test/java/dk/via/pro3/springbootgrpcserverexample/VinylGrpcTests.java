package dk.via.pro3.springbootgrpcserverexample;

import dk.via.pro3.springbootgrpcexample.generated.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.grpc.test.autoconfigure.AutoConfigureTestGrpcTransport;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.grpc.client.ImportGrpcClients;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestGrpcTransport
@ImportGrpcClients(types = VinylServiceGrpc.VinylServiceBlockingStub.class)
class VinylGrpcTests {
    @Autowired private VinylServiceGrpc.VinylServiceBlockingStub stub;

    @Test
    void addAndGetPreserveIdAndName() {
        Vinyl added = add("Kind of Blue");
        assertThat(added.getId()).isNotBlank();
        assertThat(added.getName()).isEqualTo("Kind of Blue");
        Vinyl retrieved = stub.withDeadlineAfter(5, TimeUnit.SECONDS)
                .getVinyl(GetVinylRequest.newBuilder().setId(added.getId()).build()).getVinyl();
        assertThat(retrieved).isEqualTo(added);
    }

    @Test
    void repeatedNamesProduceDistinctRecords() {
        assertThat(add("Same name").getId()).isNotEqualTo(add("Same name").getId());
    }

    @Test
    void blankNameIsInvalid() {
        assertStatus(() -> add(" \t"), Status.Code.INVALID_ARGUMENT);
    }

    @Test
    void blankIdIsInvalid() {
        assertStatus(() -> stub.withDeadlineAfter(5, TimeUnit.SECONDS)
                .getVinyl(GetVinylRequest.newBuilder().setId(" ").build()), Status.Code.INVALID_ARGUMENT);
    }

    @Test
    void unknownIdIsNotFound() {
        assertStatus(() -> stub.withDeadlineAfter(5, TimeUnit.SECONDS)
                .getVinyl(GetVinylRequest.newBuilder().setId("missing-id").build()), Status.Code.NOT_FOUND);
    }

    private Vinyl add(String name) {
        return stub.withDeadlineAfter(5, TimeUnit.SECONDS)
                .addVinyl(AddVinylRequest.newBuilder().setName(name).build()).getVinyl();
    }

    private static void assertStatus(Runnable call, Status.Code code) {
        assertThatThrownBy(call::run).isInstanceOfSatisfying(StatusRuntimeException.class,
                ex -> assertThat(ex.getStatus().getCode()).isEqualTo(code));
    }
}
