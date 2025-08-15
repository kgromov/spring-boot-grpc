package org.kgromov.grpc.service;

import com.kgromov.grpc.proto.HelloWorldRequest;
import com.kgromov.grpc.proto.HelloWorldResponse;
import com.kgromov.grpc.proto.HelloWorldServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class HelloWorldService extends HelloWorldServiceGrpc.HelloWorldServiceImplBase {

    @Override
    public void request(HelloWorldRequest request, StreamObserver<HelloWorldResponse> responseObserver) {
        HelloWorldResponse response = HelloWorldResponse.newBuilder()
                .setStatus("Hello " + request.getRequestId() + ": " + request.getMessage())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void streamRequest(HelloWorldRequest request, StreamObserver<HelloWorldResponse> responseObserver) {
        int count = 0;
        while (count < 10) {
            HelloWorldResponse response = HelloWorldResponse.newBuilder()
                    .setStatus("Hello(" + count + ") ==> " + request.getRequestId() + ": " + request.getMessage())
                    .build();
            responseObserver.onNext(response);
            count++;
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                responseObserver.onError(e);
                return;
            }
        }
        responseObserver.onCompleted();
    }
}
