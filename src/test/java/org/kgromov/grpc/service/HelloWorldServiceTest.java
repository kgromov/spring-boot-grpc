package org.kgromov.grpc.service;


import com.kgromov.grpc.proto.HelloWorldRequest;
import com.kgromov.grpc.proto.HelloWorldServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.grpc.test.autoconfigure.LocalGrpcServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.grpc.client.ImportGrpcClients;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the HelloWorldService.
 * Test over running gRPC server (not in-process) - using the actual network connection
 * `@LocalGrpcServerPort` is required to pick random port not to clash with running server
 */
@SpringBootTest(properties = {
        "spring.grpc.server.port=0",
        "spring.grpc.server.enabled=true"
})
@ImportGrpcClients(types = HelloWorldServiceGrpc.HelloWorldServiceBlockingStub.class)
class HelloWorldServiceTest {
    @LocalGrpcServerPort
    private int port;

    private HelloWorldServiceGrpc.HelloWorldServiceBlockingStub helloClient;

    @BeforeEach
    void setUp() {
        // specific
        ManagedChannel channel = NettyChannelBuilder.forTarget("localhost:%s".formatted(this.port))
                .usePlaintext()
                .build();
        // more generic
//        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", port)
//                .usePlaintext()
//                .build();
        helloClient = HelloWorldServiceGrpc.newBlockingStub(channel);
    }

    @AfterEach
    void tearDown() {
        ((ManagedChannel) helloClient.getChannel()).shutdown();
    }

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