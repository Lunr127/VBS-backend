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

public class ClientRunInfo {
    public static final String SERIALIZED_NAME_ID = "id";
    @SerializedName("id")
    private String id;
    public static final String SERIALIZED_NAME_NAME = "name";
    @SerializedName("name")
    private String name;
    public static final String SERIALIZED_NAME_DESCRIPTION = "description";
    @SerializedName("description")
    private String description;
    public static final String SERIALIZED_NAME_STATUS = "status";
    @SerializedName("status")
    private StatusEnum status;

    public ClientRunInfo() {
    }

    public ClientRunInfo id(String id) {
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

    public ClientRunInfo name(String name) {
        this.name = name;
        return this;
    }

    @ApiModelProperty(
            required = true,
            value = ""
    )
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClientRunInfo description(String description) {
        this.description = description;
        return this;
    }

    @Nullable
    @ApiModelProperty("")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ClientRunInfo status(StatusEnum status) {
        this.status = status;
        return this;
    }

    @ApiModelProperty(
            required = true,
            value = ""
    )
    public StatusEnum getStatus() {
        return this.status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            ClientRunInfo clientRunInfo = (ClientRunInfo)o;
            return Objects.equals(this.id, clientRunInfo.id) && Objects.equals(this.name, clientRunInfo.name) && Objects.equals(this.description, clientRunInfo.description) && Objects.equals(this.status, clientRunInfo.status);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.id, this.name, this.description, this.status});
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ClientRunInfo {\n");
        sb.append("    id: ").append(this.toIndentedString(this.id)).append("\n");
        sb.append("    name: ").append(this.toIndentedString(this.name)).append("\n");
        sb.append("    description: ").append(this.toIndentedString(this.description)).append("\n");
        sb.append("    status: ").append(this.toIndentedString(this.status)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }

    @JsonAdapter(StatusEnum.Adapter.class)
    public static enum StatusEnum {
        CREATED("CREATED"),
        ACTIVE("ACTIVE"),
        TERMINATED("TERMINATED");

        private String value;

        private StatusEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public String toString() {
            return String.valueOf(this.value);
        }

        public static StatusEnum fromValue(String value) {
            StatusEnum[] var1 = values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                StatusEnum b = var1[var3];
                if (b.value.equals(value)) {
                    return b;
                }
            }

            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }

        public static class Adapter extends TypeAdapter<StatusEnum> {
            public Adapter() {
            }

            public void write(JsonWriter jsonWriter, StatusEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            public StatusEnum read(JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return ClientRunInfo.StatusEnum.fromValue(value);
            }
        }
    }
}
