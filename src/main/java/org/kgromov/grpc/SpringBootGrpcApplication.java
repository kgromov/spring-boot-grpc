package org.kgromov.grpc;

import com.kgromov.grpc.proto.HelloWorldRequest;
import com.kgromov.grpc.proto.HelloWorldServiceGrpc;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.grpc.server.autoconfigure.GrpcServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.grpc.client.ImportGrpcClients;

import java.util.Random;

@ImportGrpcClients(target = "local", types = HelloWorldServiceGrpc.HelloWorldServiceBlockingStub.class)
@EnableConfigurationProperties({GrpcServerProperties.class})
@SpringBootApplication
public class SpringBootGrpcApplication {

    static void main(String[] args) {
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
            System.out.println("Received:\n" + reply.toString());
        };
    }
}
