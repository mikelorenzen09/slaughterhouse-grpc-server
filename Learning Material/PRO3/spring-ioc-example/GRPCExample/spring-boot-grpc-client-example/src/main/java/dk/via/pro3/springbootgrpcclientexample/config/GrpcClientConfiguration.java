package dk.via.pro3.springbootgrpcclientexample.config;

import dk.via.pro3.springbootgrpcexample.generated.VinylServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration(proxyBeanMethods = false)
public class GrpcClientConfiguration {
    @Bean
    public VinylServiceGrpc.VinylServiceBlockingStub vinylStub(GrpcChannelFactory channels) {
        return VinylServiceGrpc.newBlockingStub(channels.createChannel("vinyl"));
    }
}
