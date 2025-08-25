package org.kgromov.grpc.endpoints;

import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MethodSchema {
    private MessageSchema request;
    private MessageSchema response;
}
