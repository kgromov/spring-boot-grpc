package org.kgromov.grpc.config;

import com.kgromov.grpc.proto.HelloWorldServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.grpc.client.autoconfigure.GrpcClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.grpc.client.GrpcChannelBuilderCustomizer;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.grpc.client.ImportGrpcClients;

@Profile("customization")
@ImportGrpcClients(target = "local", types = HelloWorldServiceGrpc.HelloWorldServiceBlockingStub.class)
@Configuration(proxyBeanMethods = false)
public class CustomClientConfig {

    @Profile("message-channel-declaration")
    @Bean
    public ManagedChannel grpcChannel() {
        return ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
    }

    @Profile("message-channel-factory")
    @Bean
    public ManagedChannel grpcChannel(GrpcChannelFactory factory, GrpcClientProperties clientProperties) {
        // or factory.createChannel("local") + application.properties:
//        return factory.createChannel("localhost:9090");
        return factory.createChannel(clientProperties.getChannel().get("local").getTarget());
    }

    @Profile("message-channel-declaration")
    @Bean
    public HelloWorldServiceGrpc.HelloWorldServiceBlockingStub helloWorldServiceBlockingStub(ManagedChannel channel) {
        return HelloWorldServiceGrpc.newBlockingStub(channel);
    }


    @Bean
    HelloWorldServiceGrpc.HelloWorldServiceBlockingStub helloClient(
            GrpcChannelFactory channels,
            GrpcClientProperties clientProperties
    ) {
        return HelloWorldServiceGrpc.newBlockingStub(channels.createChannel(clientProperties.getChannel().get("local").getTarget()));
    }


    @Bean
    GrpcChannelBuilderCustomizer<?> helloChannelCustomizer() {
        return GrpcChannelBuilderCustomizer.matching("local",
                (builder) -> builder.usePlaintext());
    }
}
