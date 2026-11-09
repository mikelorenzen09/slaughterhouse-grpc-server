package dk.via.pro3.springiocexample.config;

import dk.via.pro3.springiocexample.model.Room;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class RoomConfiguration {
    // Scalar values come from configuration, not from component scanning.
    @Bean
    public Room room(@Value("${room.id:1}") int id,
                     @Value("${room.name:Demo Room}") String name) {
        return new Room(id, name);
    }
}
