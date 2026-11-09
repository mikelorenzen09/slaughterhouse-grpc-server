package dk.via.pro3.springbootgrpcserverexample.model;

// Domain data is separate from the generated protobuf transport messages.
public record VinylRecord(String id, String name) {
}
