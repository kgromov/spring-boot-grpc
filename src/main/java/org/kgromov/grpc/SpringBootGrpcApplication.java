package org.kgromov.grpc;

import com.kgromov.grpc.proto.HelloWorldRequest;
import com.kgromov.grpc.proto.HelloWorldServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.grpc.client.ImportGrpcClients;

@ImportGrpcClients
@SpringBootApplication
public class SpringBootGrpcApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootGrpcApplication.class, args);
    }

    @Bean
    HelloWorldServiceGrpc.HelloWorldServiceBlockingStub helloClient(GrpcChannelFactory channels) {
        return HelloWorldServiceGrpc.newBlockingStub(channels.createChannel("0.0.0.0:9090"));
    }

    @Bean
    ApplicationRunner clientRunner(HelloWorldServiceGrpc.HelloWorldServiceBlockingStub helloClient) {
        return args -> {
            var request = HelloWorldRequest.newBuilder().setMessage("Konstantin").build();
            var reply = helloClient.request(request);
            System.out.println(reply.toString());
        };
    }

    // or more specific
//    @Bean
//    public ManagedChannel grpcChannel() {
//        return ManagedChannelBuilder.forAddress("localhost", 9090)
//                .usePlaintext()
//                .build();
//    }
//
//    @Bean
//    public HelloWorldServiceGrpc.HelloWorldServiceBlockingStub helloWorldServiceBlockingStub(ManagedChannel channel) {
//        return HelloWorldServiceGrpc.newBlockingStub(channel);
//    }
//
//    @Bean
//    ApplicationRunner clientRunner(HelloWorldServiceGrpc.HelloWorldServiceBlockingStub helloClient) {
//        return args -> {
//            var request = HelloWorldRequest.newBuilder().setMessage("Konstantin").build();
//            var reply = helloClient.request(request);
//            System.out.println(reply.toString());
//        };
//    }
}
