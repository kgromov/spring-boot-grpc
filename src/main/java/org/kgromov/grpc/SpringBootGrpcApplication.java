package org.kgromov.grpc;

import com.kgromov.grpc.proto.HelloWorldRequest;
import com.kgromov.grpc.proto.HelloWorldServiceGrpc;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@SpringBootApplication
public class SpringBootGrpcApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootGrpcApplication.class, args);
    }

    @Bean
    ApplicationRunner clientRunner(HelloWorldServiceGrpc.HelloWorldServiceBlockingStub helloClient) {
        return args -> {
            var request = HelloWorldRequest.newBuilder()
                    .setRequestId(String.valueOf(Math.abs(new Random().nextInt())))
                    .setMessage("Konstantin")
                    .build();
            var reply = helloClient.request(request);
            System.out.println(reply.toString());
        };
    }
}
