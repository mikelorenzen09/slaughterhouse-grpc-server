package dk.via.pro3.springbootgrpcserverexample.service;

import dk.via.pro3.springbootgrpcserverexample.model.VinylRecord;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VinylService {
    private final Map<String, VinylRecord> vinylMap = new ConcurrentHashMap<>();

    public VinylRecord addVinyl(String name) {
        VinylRecord vinyl = new VinylRecord(UUID.randomUUID().toString(), name);
        vinylMap.put(vinyl.id(), vinyl);
        return vinyl;
    }

    public VinylRecord getVinylById(String id) {
        return vinylMap.get(id);
    }
}
