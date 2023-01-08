package whu.vbs.DRES.dev.dres.org.openapitools.client.model;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QueryEventLog {
    public static final String SERIALIZED_NAME_TIMESTAMP = "timestamp";
    @SerializedName("timestamp")
    private Long timestamp;
    public static final String SERIALIZED_NAME_EVENTS = "events";
    @SerializedName("events")
    private List<QueryEvent> events = new ArrayList();

    public QueryEventLog() {
    }

    public QueryEventLog timestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    @ApiModelProperty(
            required = true,
            value = ""
    )
    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public QueryEventLog events(List<QueryEvent> events) {
        this.events = events;
        return this;
    }

    public QueryEventLog addEventsItem(QueryEvent eventsItem) {
        this.events.add(eventsItem);
        return this;
    }

    @ApiModelProperty(
            required = true,
            value = ""
    )
    public List<QueryEvent> getEvents() {
        return this.events;
    }

    public void setEvents(List<QueryEvent> events) {
        this.events = events;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            QueryEventLog queryEventLog = (QueryEventLog)o;
            return Objects.equals(this.timestamp, queryEventLog.timestamp) && Objects.equals(this.events, queryEventLog.events);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.timestamp, this.events});
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class QueryEventLog {\n");
        sb.append("    timestamp: ").append(this.toIndentedString(this.timestamp)).append("\n");
        sb.append("    events: ").append(this.toIndentedString(this.events)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }
}
