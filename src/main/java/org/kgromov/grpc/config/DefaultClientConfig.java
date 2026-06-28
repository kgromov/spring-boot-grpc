package org.kgromov.grpc.config;

import com.kgromov.grpc.proto.HelloWorldServiceGrpc;
import org.springframework.boot.grpc.client.autoconfigure.GrpcClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.grpc.client.ImportGrpcClients;

@Profile("default")
@ImportGrpcClients(target = "local", types = HelloWorldServiceGrpc.HelloWorldServiceBlockingStub.class)
@Configuration
public class DefaultClientConfig {

    @Bean
    HelloWorldServiceGrpc.HelloWorldServiceBlockingStub helloClient(
            GrpcChannelFactory channels,
            GrpcClientProperties clientProperties
    ) {
        return HelloWorldServiceGrpc.newBlockingStub(channels.createChannel(clientProperties.getChannel().get("local").getTarget()));
    }
}
