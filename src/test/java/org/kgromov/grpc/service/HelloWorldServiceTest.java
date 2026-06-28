package org.kgromov.grpc.service;


import com.kgromov.grpc.proto.HelloWorldRequest;
import com.kgromov.grpc.proto.HelloWorldServiceGrpc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.grpc.client.autoconfigure.GrpcClientAutoConfiguration;
import org.springframework.boot.grpc.client.autoconfigure.GrpcClientProperties;
import org.springframework.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration;
import org.springframework.boot.grpc.test.autoconfigure.AutoConfigureTestGrpcTransport;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.grpc.client.ImportGrpcClients;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureTestGrpcTransport
//@Import(HelloWorldServiceTest.HelloWorldServiceTestConfiguration.class)
@ImportGrpcClients(types = HelloWorldServiceGrpc.HelloWorldServiceBlockingStub.class)
class HelloWorldServiceTest {
    @Autowired
    private HelloWorldServiceGrpc.HelloWorldServiceBlockingStub helloClient;

    @Test
    void sayHello() {
        String requestId = String.valueOf(Math.abs(new Random().nextInt()));
        var request = HelloWorldRequest.newBuilder()
                .setRequestId(requestId)
                .setMessage("Konstantin")
                .build();
        var reply = helloClient.request(request);
        assertThat(reply.getStatus()).isEqualTo("Hello " + request.getRequestId() + ": " + request.getMessage());
    }

    @TestConfiguration
    static class HelloWorldServiceTestConfiguration {
        @Bean
        HelloWorldServiceGrpc.HelloWorldServiceBlockingStub helloClient(
                GrpcChannelFactory channels,
                GrpcClientProperties clientProperties
        ) {
            return HelloWorldServiceGrpc.newBlockingStub(channels.createChannel(clientProperties.getChannel().get("local").getTarget()));
        }
    }

}