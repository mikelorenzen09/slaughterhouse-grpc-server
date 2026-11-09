package dk.via.pro3.libraryservice.model;

public class Borrower {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Integer vinylId;

    public Borrower() {
    }

    public Borrower(Long id, String firstName, String lastName, String email, Integer vinylId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.vinylId = vinylId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getVinylId() {
        return vinylId;
    }

    public void setVinylId(Integer vinylId) {
        this.vinylId = vinylId;
    }
}
