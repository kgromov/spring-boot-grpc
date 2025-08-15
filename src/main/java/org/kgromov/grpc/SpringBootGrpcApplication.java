package org.kgromov.grpc;

import com.kgromov.grpc.proto.HelloWorldRequest;
import com.kgromov.grpc.proto.HelloWorldServiceGrpc;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBootGrpcApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootGrpcApplication.class, args);
    }

    @Bean
    ApplicationRunner clientRunner(HelloWorldServiceGrpc.HelloWorldServiceBlockingStub helloClient) {
        return args -> {
            var request = HelloWorldRequest.newBuilder().setMessage("Konstantin").build();
            var reply = helloClient.request(request);
            System.out.println(reply.toString());
        };
    }
}
