package org.kgromov.grpc.endpoints;

import io.grpc.ServerServiceDefinition;
import io.grpc.ServiceDescriptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kgromov.grpc.server.GrpcServer;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Collections.emptyList;

@Component
@Endpoint(id ="grpc-metadata")
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

//    @ReadOperation
//    public CacheStatisticsDto cacheStatistics(@Selector String cacheName) {
//
//    }
}
