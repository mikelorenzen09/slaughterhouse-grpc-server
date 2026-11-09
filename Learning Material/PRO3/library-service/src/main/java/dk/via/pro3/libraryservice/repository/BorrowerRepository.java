package dk.via.pro3.libraryservice.repository;

import dk.via.pro3.libraryservice.model.Borrower;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BorrowerRepository {
    private List<Borrower> borrowers = new ArrayList<>();

    public BorrowerRepository() {
        // Sample data for vinyls
        borrowers.add(new Borrower(1L, "Bob", "Builder", "bob@builder.org", 1));
        borrowers.add(new Borrower(2L, "Dora", "Explorer", "dora@explore.org", 2));
    }

    public List<Borrower> findAll() {
        return borrowers;
    }

    public Borrower findById(Long id) {
        return borrowers.stream().filter(b -> b.getId() == id).findFirst().orElse(null);
    }

    public Borrower add(Borrower  borrower) {
        borrowers.add(borrower);
        return borrower;
    }
}
