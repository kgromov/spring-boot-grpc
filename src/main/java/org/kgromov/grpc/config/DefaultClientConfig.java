package org.kgromov.grpc.config;

import com.kgromov.grpc.proto.HelloWorldServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.grpc.client.ImportGrpcClients;

@Profile("default")
@ImportGrpcClients
@Configuration
public class DefaultClientConfig {

    @Bean
    HelloWorldServiceGrpc.HelloWorldServiceBlockingStub helloClient(GrpcChannelFactory channels) {
        return HelloWorldServiceGrpc.newBlockingStub(channels.createChannel("0.0.0.0:9090"));
    }
}
