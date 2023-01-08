package whu.vbs.DRES.dev.dres.org.openapitools.client.model;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClientRunInfoList {
    public static final String SERIALIZED_NAME_RUNS = "runs";
    @SerializedName("runs")
    private List<ClientRunInfo> runs = new ArrayList();

    public ClientRunInfoList() {
    }

    public ClientRunInfoList runs(List<ClientRunInfo> runs) {
        this.runs = runs;
        return this;
    }

    public ClientRunInfoList addRunsItem(ClientRunInfo runsItem) {
        this.runs.add(runsItem);
        return this;
    }

    @ApiModelProperty(
            required = true,
            value = ""
    )
    public List<ClientRunInfo> getRuns() {
        return this.runs;
    }

    public void setRuns(List<ClientRunInfo> runs) {
        this.runs = runs;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            ClientRunInfoList clientRunInfoList = (ClientRunInfoList)o;
            return Objects.equals(this.runs, clientRunInfoList.runs);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.runs});
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ClientRunInfoList {\n");
        sb.append("    runs: ").append(this.toIndentedString(this.runs)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }
}
