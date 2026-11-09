package dk.via.pro3.springbootgrpcclientexample.console;

import dk.via.pro3.springbootgrpcclientexample.service.VinylClientService;
import dk.via.pro3.springbootgrpcexample.generated.Vinyl;
import io.grpc.StatusRuntimeException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VinylCommands implements ApplicationRunner, ExitCodeGenerator {
    private final VinylClientService client;
    private int exitCode;

    public VinylCommands(VinylClientService client) {
        this.client = client;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            List<String> commands = args.getNonOptionArgs();
            if (commands.size() > 1) {
                throw new IllegalArgumentException("Supply one command: demo, add, or get.");
            }
            String command = commands.isEmpty() ? "demo" : commands.getFirst();
            validateOptions(args, command);
            switch (command) {
                case "demo" -> {
                    Vinyl added = client.addVinyl("Class demo vinyl");
                    print("Added", added);
                    Vinyl retrieved = client.getVinyl(added.getId());
                    print("Retrieved", retrieved);
                    if (!added.equals(retrieved)) {
                        throw new IllegalStateException("Retrieved vinyl does not match the created record.");
                    }
                    System.out.println("Demo successful: retrieved the same ID and name.");
                }
                case "add" -> print("Added", client.addVinyl(requiredValue(args, "name")));
                case "get" -> print("Retrieved", client.getVinyl(requiredValue(args, "id")));
                default -> throw new IllegalArgumentException("Unknown command: " + command);
            }
        } catch (IllegalArgumentException ex) {
            exitCode = 2;
            System.err.println("ERROR: " + ex.getMessage());
            System.err.println("Usage: [demo | add --name=\"Vinyl name\" | get --id=\"Vinyl ID\"]");
        } catch (StatusRuntimeException ex) {
            exitCode = 1;
            System.err.println("ERROR: " + ex.getStatus().getCode() + ": " + ex.getStatus().getDescription());
            switch (ex.getStatus().getCode()) {
                case UNAVAILABLE -> System.err.println("Start the gRPC server and check spring.grpc.client.channel.vinyl.target.");
                case DEADLINE_EXCEEDED -> System.err.println("The server did not respond within five seconds. Check the server connection.");
                case NOT_FOUND -> System.err.println("Check the ID. Records disappear when the server restarts.");
                default -> { }
            }
        } catch (IllegalStateException ex) {
            exitCode = 1;
            System.err.println("ERROR: " + ex.getMessage());
        }
    }

    private static void validateOptions(ApplicationArguments args, String command) {
        for (String option : args.getOptionNames()) {
            // Let Spring Boot process connection, logging and other Spring configuration overrides.
            if (option.startsWith("spring.") || option.startsWith("logging.")) {
                continue;
            }
            if (command.equals("add") && option.equals("name")
                    || command.equals("get") && option.equals("id")) {
                continue;
            }
            throw new IllegalArgumentException("Unexpected option --" + option + " for " + command + ".");
        }
    }

    private static String requiredValue(ApplicationArguments args, String option) {
        List<String> values = args.getOptionValues(option);
        if (values == null || values.size() != 1 || values.getFirst().isBlank()) {
            throw new IllegalArgumentException("Provide exactly one nonblank --" + option + " value.");
        }
        return values.getFirst();
    }

    private static void print(String action, Vinyl vinyl) {
        System.out.printf("%s vinyl: id=%s, name=%s%n", action, vinyl.getId(), vinyl.getName());
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
