package superapp.data;

import java.io.Serializable;
import java.util.Objects;

public class UserPK implements Serializable {
    /* This class creates composite primary key for User */
    private String superapp;
    private String email;

    public UserPK() {}

    public UserPK(String superapp, String email) {
        this.superapp = superapp;
        this.email = email;
    }

    public String getSuperapp() { return superapp; }

    public String getEmail() { return email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPK userPK = (UserPK) o;
        return superapp.equals(userPK.superapp) && email.equals(userPK.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(superapp, email);
    }
}
