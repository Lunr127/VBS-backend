package whu.vbs.DRES.dev.dres.org.openapitools.client.model;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

public class LoginRequest {
    public static final String SERIALIZED_NAME_USERNAME = "username";
    @SerializedName("username")
    private String username;
    public static final String SERIALIZED_NAME_PASSWORD = "password";
    @SerializedName("password")
    private String password;

    public LoginRequest() {
    }

    public LoginRequest username(String username) {
        this.username = username;
        return this;
    }

    @ApiModelProperty(
            required = true,
            value = ""
    )
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LoginRequest password(String password) {
        this.password = password;
        return this;
    }

    @ApiModelProperty(
            required = true,
            value = ""
    )
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            LoginRequest loginRequest = (LoginRequest)o;
            return Objects.equals(this.username, loginRequest.username) && Objects.equals(this.password, loginRequest.password);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.username, this.password});
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class LoginRequest {\n");
        sb.append("    username: ").append(this.toIndentedString(this.username)).append("\n");
        sb.append("    password: ").append(this.toIndentedString(this.password)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }
}
