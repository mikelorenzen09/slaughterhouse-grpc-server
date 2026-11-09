package dk.via.pro3.springbootgrpcclientexample;

import dk.via.pro3.springbootgrpcclientexample.console.VinylCommands;
import dk.via.pro3.springbootgrpcclientexample.service.VinylClientService;
import dk.via.pro3.springbootgrpcexample.generated.*;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class VinylClientTests {
    private Server server;
    private ManagedChannel channel;
    private VinylCommands commands;
    private Vinyl current;
    private boolean deadlineSeen;

    @BeforeEach
    void startTransport() throws Exception {
        String name = InProcessServerBuilder.generateName();
        server = InProcessServerBuilder.forName(name).directExecutor()
                .addService(new VinylServiceGrpc.VinylServiceImplBase() {
                    @Override
                    public void addVinyl(AddVinylRequest request, StreamObserver<VinylResponse> observer) {
                        var deadline = io.grpc.Context.current().getDeadline();
                        deadlineSeen = deadline != null && deadline.timeRemaining(TimeUnit.MILLISECONDS) <= 5000;
                        current = Vinyl.newBuilder().setId("test-id").setName(request.getName()).build();
                        observer.onNext(VinylResponse.newBuilder().setVinyl(current).build());
                        observer.onCompleted();
                    }

                    @Override
                    public void getVinyl(GetVinylRequest request, StreamObserver<VinylResponse> observer) {
                        if (current == null || !current.getId().equals(request.getId())) {
                            observer.onError(Status.NOT_FOUND.withDescription("Unknown ID").asRuntimeException());
                            return;
                        }
                        observer.onNext(VinylResponse.newBuilder().setVinyl(current).build());
                        observer.onCompleted();
                    }
                }).build().start();
        channel = InProcessChannelBuilder.forName(name).directExecutor().build();
        commands = new VinylCommands(new VinylClientService(VinylServiceGrpc.newBlockingStub(channel)));
    }

    @AfterEach
    void stopTransport() throws Exception {
        channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        server.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }

    @Test
    void noArgumentsRunsDemo(CapturedOutput output) {
        run();
        assertThat(commands.getExitCode()).isZero();
        assertThat(output.getOut()).contains("Added vinyl: id=test-id", "Retrieved vinyl: id=test-id",
                "Demo successful: retrieved the same ID and name.");
        assertThat(deadlineSeen).isTrue();
    }

    @Test
    void addAndGetAcceptNamesWithSpaces(CapturedOutput output) {
        run("add", "--name=Kind of Blue");
        run("get", "--id=test-id");
        assertThat(commands.getExitCode()).isZero();
        assertThat(current.getName()).isEqualTo("Kind of Blue");
        assertThat(output.getOut()).contains("Retrieved vinyl: id=test-id, name=Kind of Blue");
    }

    @Test
    void unknownIdReportsNotFound(CapturedOutput output) {
        run("get", "--id=missing");
        assertThat(commands.getExitCode()).isEqualTo(1);
        assertThat(output.getErr()).contains("NOT_FOUND", "Records disappear when the server restarts.");
    }

    @Test
    void stoppedServerReportsUnavailable(CapturedOutput output) throws Exception {
        server.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        run("demo");
        assertThat(commands.getExitCode()).isEqualTo(1);
        assertThat(output.getErr()).contains("UNAVAILABLE", "Start the gRPC server");
    }

    @Test
    void invalidCommandsAndArgumentsFailBeforeAnRpc(CapturedOutput output) {
        String[][] invalid = {{"unknown"}, {"add"}, {"get"}, {"add", "--name= "},
                {"get", "--id="}, {"add", "--name=a", "--name=b"}, {"demo", "--id=x"},
                {"demo", "extra"}, {"get", "--id=x", "--typo=x"}};
        for (String[] args : invalid) {
            run(args);
            assertThat(commands.getExitCode()).as(String.join(" ", args)).isEqualTo(2);
        }
        assertThat(current).isNull();
        assertThat(output.getErr()).contains("Usage:");
    }

    private void run(String... args) {
        commands.run(new DefaultApplicationArguments(args));
    }
}
