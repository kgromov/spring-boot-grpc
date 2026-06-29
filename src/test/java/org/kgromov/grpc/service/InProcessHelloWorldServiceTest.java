package org.kgromov.grpc.service;


import com.kgromov.grpc.proto.HelloWorldRequest;
import com.kgromov.grpc.proto.HelloWorldServiceGrpc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.grpc.test.autoconfigure.AutoConfigureTestGrpcTransport;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.grpc.client.ImportGrpcClients;
import org.springframework.test.context.ActiveProfiles;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the HelloWorldService in-process.
 * `@AutoConfigureTestGrpcTransport` Replace gRPC communication channels with in-process channels specifically designed for testing
 */
@ActiveProfiles("test")
@SpringBootTest(properties = "spring.grpc.server.enabled=true")
@AutoConfigureTestGrpcTransport
@ImportGrpcClients(types = HelloWorldServiceGrpc.HelloWorldServiceBlockingStub.class)
class InProcessHelloWorldServiceTest {
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
}