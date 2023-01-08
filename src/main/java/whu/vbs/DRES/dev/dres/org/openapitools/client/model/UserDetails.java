package whu.vbs.DRES.dev.dres.org.openapitools.client.model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Objects;

public class UserDetails {
    public static final String SERIALIZED_NAME_ID = "id";
    @SerializedName("id")
    private String id;
    public static final String SERIALIZED_NAME_USERNAME = "username";
    @SerializedName("username")
    private String username;
    public static final String SERIALIZED_NAME_ROLE = "role";
    @SerializedName("role")
    private RoleEnum role;
    public static final String SERIALIZED_NAME_SESSION_ID = "sessionId";
    @SerializedName("sessionId")
    private String sessionId;

    public UserDetails() {
    }

    public UserDetails id(String id) {
        this.id = id;
        return this;
    }

    @ApiModelProperty(
            required = true,
            value = ""
    )
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserDetails username(String username) {
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

    public UserDetails role(RoleEnum role) {
        this.role = role;
        return this;
    }

    @ApiModelProperty(
            required = true,
            value = ""
    )
    public RoleEnum getRole() {
        return this.role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }

    public UserDetails sessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    @Nullable
    @ApiModelProperty("")
    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            UserDetails userDetails = (UserDetails)o;
            return Objects.equals(this.id, userDetails.id) && Objects.equals(this.username, userDetails.username) && Objects.equals(this.role, userDetails.role) && Objects.equals(this.sessionId, userDetails.sessionId);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.id, this.username, this.role, this.sessionId});
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class UserDetails {\n");
        sb.append("    id: ").append(this.toIndentedString(this.id)).append("\n");
        sb.append("    username: ").append(this.toIndentedString(this.username)).append("\n");
        sb.append("    role: ").append(this.toIndentedString(this.role)).append("\n");
        sb.append("    sessionId: ").append(this.toIndentedString(this.sessionId)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }

    @JsonAdapter(RoleEnum.Adapter.class)
    public static enum RoleEnum {
        ADMIN("ADMIN"),
        JUDGE("JUDGE"),
        VIEWER("VIEWER"),
        PARTICIPANT("PARTICIPANT");

        private String value;

        private RoleEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public String toString() {
            return String.valueOf(this.value);
        }

        public static RoleEnum fromValue(String value) {
            RoleEnum[] var1 = values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                RoleEnum b = var1[var3];
                if (b.value.equals(value)) {
                    return b;
                }
            }

            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }

        public static class Adapter extends TypeAdapter<RoleEnum> {
            public Adapter() {
            }

            public void write(JsonWriter jsonWriter, RoleEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            public RoleEnum read(JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return UserDetails.RoleEnum.fromValue(value);
            }
        }
    }
}
