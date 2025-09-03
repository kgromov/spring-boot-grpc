package org.kgromov.grpc.endpoints;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.stereotype.Component;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Component
@Endpoint(id = "grpc-message-metadata")
@Slf4j
@RequiredArgsConstructor
public class GrpcMessageMetadataEndpoint {

    @SneakyThrows
    @ReadOperation
    public MessageSchema messageSchema(@Selector String messageType) {
        Class<?> messageClass = Class.forName(messageType);
        var fields = Stream.of(messageClass.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> !field.getName().equals("memoizedIsInitialized"))
                .map(field -> FieldSchema.builder().name(field.getName()).type(field.getType().getTypeName()).build())
                .toList();
        // TODO: match
        Map<String, String> fieldsByType = this.analyzeType(messageClass);
        return MessageSchema.builder()
                .type(messageClass.getTypeName())
                .fields(fields)
                .build();
    }

    private Map<String, String> analyzeType(Class<?> type) {
        try {
            Map<String, String> fieldToGetter = new HashMap<>();
            BeanInfo beanInfo = Introspector.getBeanInfo(type, Object.class);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                String propertyName = propertyDescriptor.getName();
                Class<?> propertyType = propertyDescriptor.getPropertyType();
                fieldToGetter.put(propertyName, propertyType.getTypeName());
            }
            return fieldToGetter;
        } catch (Exception e) {
            log.error("Can't introspect class = {}", type, e);
            throw new RuntimeException(e);
        }
    }

}
