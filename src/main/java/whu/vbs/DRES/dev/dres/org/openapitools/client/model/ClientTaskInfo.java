package whu.vbs.DRES.dev.dres.org.openapitools.client.model;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

public class ClientTaskInfo {
    public static final String SERIALIZED_NAME_ID = "id";
    @SerializedName("id")
    private String id;
    public static final String SERIALIZED_NAME_NAME = "name";
    @SerializedName("name")
    private String name;
    public static final String SERIALIZED_NAME_TASK_GROUP = "taskGroup";
    @SerializedName("taskGroup")
    private String taskGroup;
    public static final String SERIALIZED_NAME_REMAINING_TIME = "remainingTime";
    @SerializedName("remainingTime")
    private Long remainingTime;
    public static final String SERIALIZED_NAME_RUNNING = "running";
    @SerializedName("running")
    private Boolean running;

    public ClientTaskInfo() {
    }

    public ClientTaskInfo id(String id) {
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

    public ClientTaskInfo name(String name) {
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

    public ClientTaskInfo taskGroup(String taskGroup) {
        this.taskGroup = taskGroup;
        return this;
    }

    @ApiModelProperty(
            required = true,
            value = ""
    )
    public String getTaskGroup() {
        return this.taskGroup;
    }

    public void setTaskGroup(String taskGroup) {
        this.taskGroup = taskGroup;
    }

    public ClientTaskInfo remainingTime(Long remainingTime) {
        this.remainingTime = remainingTime;
        return this;
    }

    @ApiModelProperty(
            required = true,
            value = ""
    )
    public Long getRemainingTime() {
        return this.remainingTime;
    }

    public void setRemainingTime(Long remainingTime) {
        this.remainingTime = remainingTime;
    }

    public ClientTaskInfo running(Boolean running) {
        this.running = running;
        return this;
    }

    @ApiModelProperty(
            required = true,
            value = ""
    )
    public Boolean getRunning() {
        return this.running;
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            ClientTaskInfo clientTaskInfo = (ClientTaskInfo)o;
            return Objects.equals(this.id, clientTaskInfo.id) && Objects.equals(this.name, clientTaskInfo.name) && Objects.equals(this.taskGroup, clientTaskInfo.taskGroup) && Objects.equals(this.remainingTime, clientTaskInfo.remainingTime) && Objects.equals(this.running, clientTaskInfo.running);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.id, this.name, this.taskGroup, this.remainingTime, this.running});
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ClientTaskInfo {\n");
        sb.append("    id: ").append(this.toIndentedString(this.id)).append("\n");
        sb.append("    name: ").append(this.toIndentedString(this.name)).append("\n");
        sb.append("    taskGroup: ").append(this.toIndentedString(this.taskGroup)).append("\n");
        sb.append("    remainingTime: ").append(this.toIndentedString(this.remainingTime)).append("\n");
        sb.append("    running: ").append(this.toIndentedString(this.running)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }
}
