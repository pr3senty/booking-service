package edu.centraluniversity.app;

import edu.centraluniversity.AuthGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @Value("${grpc.server.host}")
    private String grpcHost;

    @Value("${grpc.server.port}")
    private int grpcPort;

    @Bean
    public ManagedChannel grpcChannel() {
        return ManagedChannelBuilder
                .forAddress(grpcHost, grpcPort)
                .usePlaintext()
                .build();
    }

    @Bean
    public AuthGrpc.AuthBlockingStub authBlockingStub(ManagedChannel channel) {
        return AuthGrpc.newBlockingStub(channel);
    }
}
