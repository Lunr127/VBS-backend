package whu.vbs.DRES.dev.dres.org.openapitools.client.model;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

public class SuccessStatus {
    public static final String SERIALIZED_NAME_DESCRIPTION = "description";
    @SerializedName("description")
    private String description;
    public static final String SERIALIZED_NAME_STATUS = "status";
    @SerializedName("status")
    private Boolean status;

    public SuccessStatus() {
    }

    public SuccessStatus description(String description) {
        this.description = description;
        return this;
    }

    @ApiModelProperty(
            required = true,
            value = ""
    )
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SuccessStatus status(Boolean status) {
        this.status = status;
        return this;
    }

    @ApiModelProperty(
            required = true,
            value = ""
    )
    public Boolean getStatus() {
        return this.status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            SuccessStatus successStatus = (SuccessStatus)o;
            return Objects.equals(this.description, successStatus.description) && Objects.equals(this.status, successStatus.status);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.description, this.status});
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SuccessStatus {\n");
        sb.append("    description: ").append(this.toIndentedString(this.description)).append("\n");
        sb.append("    status: ").append(this.toIndentedString(this.status)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }
}
