package org.kgromov.grpc.config;


import io.grpc.Status;
import io.grpc.StatusException;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
public class CustomGrpcExceptionHandler implements GrpcExceptionHandler {

    @Override
    public StatusException handleException(Throwable exception) {
        if (exception instanceof HttpClientErrorException.NotFound) {
            return new StatusException(Status.NOT_FOUND);
        } else if (exception instanceof StatusException) {
            return new StatusException(Status.fromThrowable(exception));
        }
        return new StatusException(Status.fromThrowable(exception));
    }
}
