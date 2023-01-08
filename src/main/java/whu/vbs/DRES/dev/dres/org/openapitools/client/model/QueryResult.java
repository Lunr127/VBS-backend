package whu.vbs.DRES.dev.dres.org.openapitools.client.model;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Nullable;
import java.util.Objects;

public class QueryResult {
    public static final String SERIALIZED_NAME_ITEM = "item";
    @SerializedName("item")
    private String item;
    public static final String SERIALIZED_NAME_SEGMENT = "segment";
    @SerializedName("segment")
    private Integer segment;
    public static final String SERIALIZED_NAME_FRAME = "frame";
    @SerializedName("frame")
    private Integer frame;
    public static final String SERIALIZED_NAME_SCORE = "score";
    @SerializedName("score")
    private Double score;
    public static final String SERIALIZED_NAME_RANK = "rank";
    @SerializedName("rank")
    private Integer rank;

    public QueryResult() {
    }

    public QueryResult item(String item) {
        this.item = item;
        return this;
    }

    @ApiModelProperty(
            required = true,
            value = ""
    )
    public String getItem() {
        return this.item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public QueryResult segment(Integer segment) {
        this.segment = segment;
        return this;
    }

    @Nullable
    @ApiModelProperty("")
    public Integer getSegment() {
        return this.segment;
    }

    public void setSegment(Integer segment) {
        this.segment = segment;
    }

    public QueryResult frame(Integer frame) {
        this.frame = frame;
        return this;
    }

    @Nullable
    @ApiModelProperty("")
    public Integer getFrame() {
        return this.frame;
    }

    public void setFrame(Integer frame) {
        this.frame = frame;
    }

    public QueryResult score(Double score) {
        this.score = score;
        return this;
    }

    @Nullable
    @ApiModelProperty("")
    public Double getScore() {
        return this.score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public QueryResult rank(Integer rank) {
        this.rank = rank;
        return this;
    }

    @Nullable
    @ApiModelProperty("")
    public Integer getRank() {
        return this.rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            QueryResult queryResult = (QueryResult)o;
            return Objects.equals(this.item, queryResult.item) && Objects.equals(this.segment, queryResult.segment) && Objects.equals(this.frame, queryResult.frame) && Objects.equals(this.score, queryResult.score) && Objects.equals(this.rank, queryResult.rank);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.item, this.segment, this.frame, this.score, this.rank});
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class QueryResult {\n");
        sb.append("    item: ").append(this.toIndentedString(this.item)).append("\n");
        sb.append("    segment: ").append(this.toIndentedString(this.segment)).append("\n");
        sb.append("    frame: ").append(this.toIndentedString(this.frame)).append("\n");
        sb.append("    score: ").append(this.toIndentedString(this.score)).append("\n");
        sb.append("    rank: ").append(this.toIndentedString(this.rank)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }
}
