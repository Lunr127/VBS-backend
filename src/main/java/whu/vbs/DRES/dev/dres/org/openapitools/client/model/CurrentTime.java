package whu.vbs.DRES.dev.dres.org.openapitools.client.model;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

public class CurrentTime {
    public static final String SERIALIZED_NAME_TIME_STAMP = "timeStamp";
    @SerializedName("timeStamp")
    private Long timeStamp;

    public CurrentTime() {
    }

    public CurrentTime timeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    @ApiModelProperty(
            required = true,
            value = ""
    )
    public Long getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            CurrentTime currentTime = (CurrentTime)o;
            return Objects.equals(this.timeStamp, currentTime.timeStamp);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.timeStamp});
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class CurrentTime {\n");
        sb.append("    timeStamp: ").append(this.toIndentedString(this.timeStamp)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }
}
