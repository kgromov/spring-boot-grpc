package org.kgromov.grpc.endpoints;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MethodSchema {
    private String name;
    private String type;
    private MessageSchema request;
    private MessageSchema response;
}
