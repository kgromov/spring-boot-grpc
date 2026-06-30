package org.kgromov.grpc.service;


import com.kgromov.grpc.proto.HelloWorldRequest;
import com.kgromov.grpc.proto.HelloWorldServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This is basically verbose explicit implementation of InProcessHelloWorldService
 * The same can be achieved by using `@@AutoConfigureInProcessTransport` - see {@link HelloWorldServiceTest}
 */
class InProcessHelloWorldServiceServerTest {
    private HelloWorldServiceGrpc.HelloWorldServiceBlockingStub helloWorldServiceStub;
    private Server inProcessServer;
    private ManagedChannel managedChannel;

    @BeforeEach
    void setup() throws IOException {
        String serviceName = InProcessServerBuilder.generateName();

        inProcessServer = InProcessServerBuilder.forName(serviceName)
                .directExecutor()
                .addService(new HelloWorldService())
                .build()
                .start();

        managedChannel = InProcessChannelBuilder.forName(serviceName)
                .directExecutor()
                .usePlaintext()
                .build();
        helloWorldServiceStub = HelloWorldServiceGrpc.newBlockingStub(managedChannel);
    }

    @AfterEach
    void tearDown() {
        inProcessServer.shutdown();
        managedChannel.shutdown();
    }

    @Test
    void request_ValidRequest_ExpectResponseImplementedByService() {
        String requestId = String.valueOf(Math.abs(new Random().nextInt()));
        var request = HelloWorldRequest.newBuilder()
                .setRequestId(requestId)
                .setMessage("Konstantin")
                .build();
        var reply = helloWorldServiceStub.request(request);
        assertThat(reply.getStatus()).isEqualTo("Hello " + request.getRequestId() + ": " + request.getMessage());
    }
}