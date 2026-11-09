package dk.via.pro3.restapiexample.repository;

import dk.via.pro3.restapiexample.model.Vinyl;
import org.springframework.stereotype.Repository;

@Repository
public class VinylDAO {
    private static VinylList vinyls = new VinylList();

    static {
        // Initialize with sample vinyl records
        vinyls.getVinylList()
                .add(new Vinyl(1, "Legend", "Bob Marley and the Wailers",
                        2014
                ));
        vinyls.getVinylList()
                .add(new Vinyl(2, "Thriller", "Michael Jackson",
                        1980
                ));
        vinyls.getVinylList()
                .add(new Vinyl(3, "The Art of Loving", "Olivia Dean",
                        2025
                ));
        vinyls.getVinylList()
                .add(new Vinyl(4, "Never Mind", "Nirvana",
                        2014
                ));
    }

    // Retrieve all vinyls
    public VinylList getAllVinyls() {
        return vinyls;
    }

    public Vinyl getVinylById(int id) {
        // return vinyls.getVinylList().get(id);
        return vinyls.getVinylList()
                .stream()
                .filter(v -> v.getId() == id)
                .findFirst().orElse(null);
    }

    // Add a vinyl record/plate
    public void addVinyl(Vinyl vinyl) {
        vinyl.setId(vinyls.getVinylList().size() + 1);
        vinyls.getVinylList().add(vinyl);
    }
}
