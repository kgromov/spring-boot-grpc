package org.kgromov.grpc.endpoints;

import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class FieldsSchema {
    private String name;
    private String type;
}
