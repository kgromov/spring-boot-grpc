# Spring Boot gRPC

A demonstration project showcasing gRPC integration with Spring Boot 3.5.4, featuring both server and client implementations with multiple configuration approaches.

## Features

- **gRPC Server**: Hello World service with both unary and streaming RPC methods
- **gRPC Client**: Multiple configuration approaches for client setup
- **Spring Boot Integration**: Leverages Spring gRPC starter for seamless integration
- **Health Checks**: Built-in gRPC health checking support
- **Actuator Integration**: Management endpoints for monitoring
- **Protocol Buffers**: Auto-generated Java classes from proto definitions

## Technology Stack

- **Java 24**
- **Spring Boot 3.5.4**
- **Spring gRPC 0.10.0**
- **gRPC 1.72.0**
- **Protocol Buffers 4.30.2**
- **Maven** for dependency management

## Project Structure

```
src/
├── main/
│   ├── java/org/kgromov/grpc/
│   │   ├── SpringBootGrpcApplication.java     # Main application class
│   │   ├── config/                            # Client configuration classes
│   │   │   ├── DefaultClientConfig.java
│   │   │   ├── MessageChannelDeclarationClientConfig.java
│   │   │   └── MessageChannelFactoryClientConfig.java
│   │   └── service/
│   │       └── HelloWorldService.java         # gRPC service implementation
│   ├── proto/
│   │   └── hello_world.proto                  # Protocol buffer definition
│   └── resources/
│       └── application.properties             # Configuration properties
└── test/
    └── java/org/kgromov/grpc/
        └── SpringBootGrpcApplicationTests.java
```

## Getting Started

### Prerequisites

- Java 24 or higher
- Maven 3.6+

### Installation & Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd spring-boot-grpc
   ```

2. **Generate Protocol Buffer classes**
   ```bash
   mvn clean compile
   ```
   This will generate Java classes from the `.proto` files in `src/main/proto/`.

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The application will start with:
- gRPC server on port `9090` (default)
- HTTP actuator endpoints on port `9999`
- Web server on port `8080` (default Spring Boot port)

## gRPC Service

### Hello World Service

The service provides two RPC methods:

#### 1. Unary RPC - `request`
```protobuf
rpc request(HelloWorldRequest) returns (HelloWorldResponse) {}
```

#### 2. Server Streaming RPC - `streamRequest`
```protobuf
rpc streamRequest(HelloWorldRequest) returns (stream HelloWorldResponse) {}
```

### Protocol Buffer Messages

```protobuf
message HelloWorldRequest {
  string requestId = 1;
  string message = 2;
}

message HelloWorldResponse {
  string status = 1;
}
```

## Client Configuration Approaches

The project demonstrates three different approaches to configure gRPC clients:

### 1. Default Client Configuration (Profile: `default`)
Uses Spring gRPC's automatic channel factory with `@ImportGrpcClients`:

```java
@Bean
HelloWorldServiceGrpc.HelloWorldServiceBlockingStub helloClient(GrpcChannelFactory channels) {
    return HelloWorldServiceGrpc.newBlockingStub(channels.createChannel("0.0.0.0:9090"));
}
```

### 2. Manual Channel Declaration (Profile: `message-channel-declaration`)
Direct `ManagedChannel` creation with `ManagedChannelBuilder`:

```java
@Bean
public ManagedChannel grpcChannel() {
    return ManagedChannelBuilder.forAddress("localhost", 9090)
            .usePlaintext()
            .build();
}
```

### 3. Channel Factory Approach (Profile: `message-channel-factory`)
Uses `GrpcChannelFactory` for channel creation:

```java
@Bean
public ManagedChannel grpcChannel(GrpcChannelFactory factory) {
    return factory.createChannel("localhost:9090");
}
```

## Configuration

### Application Properties

```properties
# Application name
spring.application.name=spring-boot-grpc

# gRPC client configuration
spring.grpc.client.default-channel.health.enabled=true
spring.grpc.client.channels.local.address=0.0.0.0:9090

# Actuator configuration
management.endpoint.health.show-details=always
management.endpoints.web.exposure.include=*
management.server.port=9999
```

### Switching Client Configurations

Use Spring profiles to switch between different client configurations:

```bash
# Default configuration
mvn spring-boot:run

# Manual channel declaration
mvn spring-boot:run -Dspring.profiles.active=message-channel-declaration

# Channel factory approach
mvn spring-boot:run -Dspring.profiles.active=message-channel-factory
```

## Usage Example

When the application starts, it automatically executes a client call demonstrating the gRPC communication:

```java
@Bean
ApplicationRunner clientRunner(HelloWorldServiceGrpc.HelloWorldServiceBlockingStub helloClient) {
    return args -> {
        var request = HelloWorldRequest.newBuilder()
                .setRequestId(String.valueOf(Math.abs(new Random().nextInt())))
                .setMessage("Konstantin")
                .build();
        var reply = helloClient.request(request);
        System.out.println(reply.toString());
    };
}
```

Expected output:
```
status: "Hello 123456789: Konstantin"
```

## Health Checks

The application includes gRPC health checking support. You can check the health status via:

- **Actuator endpoint**: `http://localhost:9999/actuator/health`
- **gRPC health service**: Available on the gRPC port (9090)

## Testing

Run the test suite:

```bash
mvn test
```

The project includes integration tests using Spring gRPC test support.

## Development

### Adding New Services

1. Define your service in a `.proto` file under `src/main/proto/`
2. Run `mvn compile` to generate Java classes
3. Implement the service by extending the generated base class
4. Annotate with `@GrpcService`

### Custom Interceptors

You can add gRPC interceptors by implementing `ServerInterceptor` or `ClientInterceptor` and registering them as Spring beans.

## Monitoring

Access management endpoints at `http://localhost:9999/actuator/`:

- `/health` - Application health status
- `/metrics` - Application metrics
- `/info` - Application information

## Useful Resources

- [Spring gRPC Documentation](https://docs.spring.io/spring-grpc/docs/current/reference/html/)
- [gRPC Java Documentation](https://grpc.io/docs/languages/java/)
- [Protocol Buffers Documentation](https://developers.google.com/protocol-buffers)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
