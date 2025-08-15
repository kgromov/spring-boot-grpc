package org.kgromov.grpc.config;

import com.kgromov.grpc.proto.HelloWorldServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("message-channel-declaration")
@Configuration
public class MessageChannelDeclarationClientConfig {

    @Bean
    public ManagedChannel grpcChannel() {
        return ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
    }

    @Bean
    public HelloWorldServiceGrpc.HelloWorldServiceBlockingStub helloWorldServiceBlockingStub(ManagedChannel channel) {
        return HelloWorldServiceGrpc.newBlockingStub(channel);
    }

}
