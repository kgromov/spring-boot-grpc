package org.kgromov.grpc.config;

import com.kgromov.grpc.proto.HelloWorldServiceGrpc;
import io.grpc.ManagedChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.grpc.client.GrpcChannelFactory;

@Profile("message-channel-factory")
@Configuration
public class MessageChannelFactoryClientConfig {

    @Bean
    public ManagedChannel grpcChannel(GrpcChannelFactory factory) {
        // or factory.createChannel("local") + application.properties:
        // spring.grpc.client.channels.local.address=0.0.0.0:9090
        return factory.createChannel("localhost:9090");
    }

    @Bean
    public HelloWorldServiceGrpc.HelloWorldServiceBlockingStub helloWorldServiceBlockingStub(ManagedChannel channel) {
        return HelloWorldServiceGrpc.newBlockingStub(channel);
    }

}
