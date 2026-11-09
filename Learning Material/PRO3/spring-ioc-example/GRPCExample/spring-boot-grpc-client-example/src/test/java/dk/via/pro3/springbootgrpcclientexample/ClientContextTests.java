package dk.via.pro3.springbootgrpcclientexample;

import dk.via.pro3.springbootgrpcclientexample.console.VinylCommands;
import dk.via.pro3.springbootgrpcexample.generated.VinylServiceGrpc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

// Exercise the real Boot channel configuration without requiring a running server.
@SpringBootTest(args = "unknown")
class ClientContextTests {
    @Autowired private VinylServiceGrpc.VinylServiceBlockingStub stub;
    @Autowired private VinylCommands commands;

    @Test
    void realChannelAndStubAreCreatedAndInvalidCommandIsHandled() {
        assertThat(stub).isNotNull();
        assertThat(commands.getExitCode()).isEqualTo(2);
    }
}
