package org.kgromov.grpc.endpoints;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ServiceMethods {
    private String name;
    private List<MethodSchema> methods;
}
