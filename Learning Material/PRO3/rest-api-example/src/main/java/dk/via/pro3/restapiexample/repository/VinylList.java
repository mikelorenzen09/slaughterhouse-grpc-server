package dk.via.pro3.restapiexample.repository;

import dk.via.pro3.restapiexample.model.Vinyl;

import java.util.ArrayList;
import java.util.List;

public class VinylList {
    private List<Vinyl> vinylList;

    // Get the vinyl list (initialize if null)
    public List<Vinyl> getVinylList() {
        if (vinylList == null) {
            vinylList = new ArrayList<>();
        }
        return vinylList;
    }

    public void setVinylList(List<Vinyl> vinylList) {
        this.vinylList = vinylList;
    }
}
