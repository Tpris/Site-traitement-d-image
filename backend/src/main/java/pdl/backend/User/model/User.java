package pdl.backend.User.model;

import pdl.backend.Image;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @NotBlank
    @Size(max = 20)
    private final String username;
    @NotBlank
    @Size(max = 120)
    private final String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() {
        this.username = "";
        this.password = "";
    }

    public Long getId() {
        return Id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setId(Long id) {
        this.Id = id;
    }
}