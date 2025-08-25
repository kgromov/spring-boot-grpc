package org.kgromov.grpc.server;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class GrpcServer {

    private final Server server;

    @Value("${grpc.server.port:6565}")
    private int grpcPort;

    public GrpcServer( BindableService... services) {
        ServerBuilder<?> serverBuilder = ServerBuilder.forPort(grpcPort);
        List.of(services).forEach(serverBuilder::addService);
        this.server = serverBuilder.build();
    }

    @PostConstruct
    public void start() throws IOException {
        this.server.start();
        log.info("gRPC server started on port: {}", grpcPort);
    }

    @PreDestroy
    public void stop() {
        if (server != null) {
            log.info("Shutting down gRPC server");
            server.shutdown();
            log.info("gRPC server shut down");
        }
    }

    public List<ServerServiceDefinition> getServiceDefinitions() {
        return server.getServices();
    }
}