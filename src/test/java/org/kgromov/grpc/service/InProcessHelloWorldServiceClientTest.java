package org.kgromov.grpc.service;


import com.kgromov.grpc.proto.HelloWorldRequest;
import com.kgromov.grpc.proto.HelloWorldResponse;
import com.kgromov.grpc.proto.HelloWorldServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class InProcessHelloWorldServiceClientTest {
    private Server inProcessServer;
    private ManagedChannel managedChannel;
    private HelloWorldServiceGrpc.HelloWorldServiceImplBase mockService;
    private HelloWorldClient helloWorldClient;


    @BeforeEach
    void setup() throws IOException {
        String serverName = InProcessServerBuilder.generateName();
        mockService = spy(HelloWorldServiceGrpc.HelloWorldServiceImplBase.class);

        inProcessServer = InProcessServerBuilder
                .forName(serverName)
                .directExecutor()
                .addService(mockService.bindService())
                .build()
                .start();

        managedChannel = InProcessChannelBuilder.forName(serverName)
                .directExecutor()
                .usePlaintext()
                .build();

        helloWorldClient = new InProcessHelloWorldServiceClientTest.HelloWorldClient(managedChannel);
    }

    @AfterEach
    void tearDown() {
        inProcessServer.shutdown();
        managedChannel.shutdown();
    }

    @Test
    void mockRequest_ValidRequest_ExpectMockedResponse() {
        String requestId = String.valueOf(Math.abs(new Random().nextInt()));
        var request = HelloWorldRequest.newBuilder()
                .setRequestId(requestId)
                .setMessage("Konstantin")
                .build();

        this.mockHelloWorldService(request);
        HelloWorldResponse response = helloWorldClient.helloWorld(request);

        assertThat(response.getStatus()).isEqualTo("Mocked - " + request.getMessage());
    }

    private void mockHelloWorldService(HelloWorldRequest request) {
        Mockito.doAnswer(invocation -> {
            StreamObserver<HelloWorldResponse> observer = invocation.getArgument(1);
            HelloWorldResponse response = HelloWorldResponse.newBuilder()
                    .setStatus("Mocked - " + request.getMessage())
                    .build();
            observer.onNext(response);
            observer.onCompleted();
            return null;
//        }).when(mockService).request(any(HelloWorldRequest.class), any(StreamObserver.class));
        }).when(mockService).request(any(HelloWorldRequest.class), any());
    }


    private record HelloWorldClient(
            HelloWorldServiceGrpc.HelloWorldServiceBlockingStub helloWorldServiceStub,
            ManagedChannel managedChannel) {

        public HelloWorldClient(ManagedChannel managedChannel) {
            this(HelloWorldServiceGrpc.newBlockingStub(managedChannel), managedChannel);
        }

        public HelloWorldResponse helloWorld(HelloWorldRequest request) {
            return helloWorldServiceStub.request(request);
        }
    }
}