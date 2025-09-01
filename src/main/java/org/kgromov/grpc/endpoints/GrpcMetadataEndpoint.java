package org.kgromov.grpc.endpoints;

import com.google.protobuf.Descriptors;
import io.grpc.MethodDescriptor;
import io.grpc.ServerServiceDefinition;
import io.grpc.ServiceDescriptor;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.protobuf.lite.ProtoLiteUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kgromov.grpc.server.GrpcServer;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
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
                .peek(md -> {
//                    try {
//                        Class<?> messageClass = Class.forName(requestType.getName());
//                        com.google.protobuf.GeneratedMessage defaultInstance = (com.google.protobuf.GeneratedMessage) messageClass.getMethod("getDefaultInstance").invoke(null);
//                        Descriptors.FieldDescriptor fieldByNumber = defaultInstance.getDescriptorForType().findFieldByNumber(0)
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
                })
                .map(md -> {
                    MethodDescriptor.Marshaller<?> requestMarshaller = md.getRequestMarshaller();
                    MethodDescriptor.Marshaller<?> responseMarshaller = md.getResponseMarshaller();
                    Class<?> requestType = ((MethodDescriptor.ReflectableMarshaller) requestMarshaller).getMessageClass();
                    Class<?> responseType = ((MethodDescriptor.ReflectableMarshaller) responseMarshaller).getMessageClass();
                    return MethodSchema.builder()
                            .name(md.getBareMethodName())
                            .type(md.getType().name())
                            .request(MessageSchema.builder().type(requestType.getTypeName()).build())
                            .response(MessageSchema.builder().type(responseType.getTypeName()).build())
                            .build();
                })
                .toList();
        return ServiceMethods.builder()
                .name(serviceName)
                .methods(methods)
                .build();
    }
}
