package ibf2022.batch2.ssf.frontcontroller.model;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.constraints.Size;

public class Login {

    @Size(min = 2, message = "Username should be at least 2 characters.")
    private String username;

    @Size(min = 2, message = "Password should be at least 2 characters.")
    private String password;

    private Double captchaAnswer;

    public Login() {
    }

    public Login(@Size(min = 2, message = "Username should be at least 2 characters.") String username,
            @Size(min = 2, message = "Password should be at least 2 characters.") String password) {
        this.username = username;
        this.password = password;
    }

    public Login(@Size(min = 2, message = "Username should be at least 2 characters.") String username,
            @Size(min = 2, message = "Password should be at least 2 characters.") String password,
            Double captchaAnswer) {
        this.username = username;
        this.password = password;
        this.captchaAnswer = captchaAnswer;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getCaptchaAnswer() {
        return captchaAnswer;
    }

    public void setCaptchaAnswer(Double captchaAnswer) {
        this.captchaAnswer = captchaAnswer;
    }

    @Override
    public String toString() {
        return "Login [username=" + username + ", password=" + password + "]";
    }

    public JsonObject toJSON() {
        return Json.createObjectBuilder()
                .add("username", this.getUsername())
                .add("password", this.getPassword())
                .build();
    }

    public String toJSONString() {
        return this.toJSON().toString();
    }
}
