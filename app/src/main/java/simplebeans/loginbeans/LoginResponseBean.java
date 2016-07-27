package simplebeans.loginbeans;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Owner on 7/9/2016.
 */
public class LoginResponseBean {
    @JsonProperty("fName")
    private String firstName;
    @JsonProperty("otherNames")
    private String lastName;
    @JsonProperty("token")
    private String token;
    @JsonProperty("email")
    private String email;

    public LoginResponseBean() {
    }

    public LoginResponseBean(String firstName, String lastName, String token, String email) {

        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setToken(token);
        this.setEmail(email);
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
