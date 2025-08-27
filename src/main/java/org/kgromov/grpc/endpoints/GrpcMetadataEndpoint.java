package org.kgromov.grpc.endpoints;

import io.grpc.MethodDescriptor;
import io.grpc.ServerServiceDefinition;
import io.grpc.ServiceDescriptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kgromov.grpc.server.GrpcServer;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@Endpoint(id = "grpc-metadata")
@Slf4j
@RequiredArgsConstructor
public class GrpcMetadataEndpoint {
    private final GrpcServer grpcServer;

    @ReadOperation
    public List<String> serviceNames() {
        return this.grpcServer.getServiceDefinitions()
                .stream()
                .map(ServerServiceDefinition::getServiceDescriptor)
                .map(ServiceDescriptor::getName)
                .toList();
    }

    @ReadOperation
    public ServiceMethods serviceMethods(@Selector String serviceName) {
        var methods = this.grpcServer.getServiceDefinitions()
                .stream()
                .map(ServerServiceDefinition::getServiceDescriptor)
                .filter(serviceDescriptor -> serviceDescriptor.getName().equals(serviceName))
                .map(ServiceDescriptor::getMethods)
                .flatMap(Collection::stream)
                .peek(md -> log.info("Method schema: {}", md.getSchemaDescriptor()))
                .map(md -> MethodSchema.builder()
                        .name(md.getBareMethodName())
                        .type(md.getType().name())
                        .build())
                .toList();
        return ServiceMethods.builder()
                .name(serviceName)
                .methods(methods)
                .build();
    }
}
