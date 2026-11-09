package dk.via.pro3.springbootgrpcclientexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringBootGrpcClientExampleApplication {
    public static void main(String[] args) {
        int exitCode;
        try (ConfigurableApplicationContext context = SpringApplication.run(
                SpringBootGrpcClientExampleApplication.class, args)) {
            exitCode = SpringApplication.exit(context);
        }
        System.exit(exitCode);
    }
}
