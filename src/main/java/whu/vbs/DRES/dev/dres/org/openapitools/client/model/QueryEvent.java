package whu.vbs.DRES.dev.dres.org.openapitools.client.model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.annotations.ApiModelProperty;

import java.io.IOException;
import java.util.Objects;

public class QueryEvent {
    public static final String SERIALIZED_NAME_TIMESTAMP = "timestamp";
    @SerializedName("timestamp")
    private Long timestamp;
    public static final String SERIALIZED_NAME_CATEGORY = "category";
    @SerializedName("category")
    private CategoryEnum category;
    public static final String SERIALIZED_NAME_TYPE = "type";
    @SerializedName("type")
    private String type;
    public static final String SERIALIZED_NAME_VALUE = "value";
    @SerializedName("value")
    private String value;

    public QueryEvent() {
    }

    public QueryEvent timestamp(Long timestamp) {
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

    public QueryEvent category(CategoryEnum category) {
        this.category = category;
        return this;
    }

    @ApiModelProperty(
            required = true,
            value = ""
    )
    public CategoryEnum getCategory() {
        return this.category;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public QueryEvent type(String type) {
        this.type = type;
        return this;
    }

    @ApiModelProperty(
            required = true,
            value = ""
    )
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public QueryEvent value(String value) {
        this.value = value;
        return this;
    }

    @ApiModelProperty(
            required = true,
            value = ""
    )
    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            QueryEvent queryEvent = (QueryEvent)o;
            return Objects.equals(this.timestamp, queryEvent.timestamp) && Objects.equals(this.category, queryEvent.category) && Objects.equals(this.type, queryEvent.type) && Objects.equals(this.value, queryEvent.value);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.timestamp, this.category, this.type, this.value});
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class QueryEvent {\n");
        sb.append("    timestamp: ").append(this.toIndentedString(this.timestamp)).append("\n");
        sb.append("    category: ").append(this.toIndentedString(this.category)).append("\n");
        sb.append("    type: ").append(this.toIndentedString(this.type)).append("\n");
        sb.append("    value: ").append(this.toIndentedString(this.value)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }

    @JsonAdapter(CategoryEnum.Adapter.class)
    public static enum CategoryEnum {
        TEXT("TEXT"),
        IMAGE("IMAGE"),
        SKETCH("SKETCH"),
        FILTER("FILTER"),
        BROWSING("BROWSING"),
        COOPERATION("COOPERATION"),
        OTHER("OTHER");

        private String value;

        private CategoryEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public String toString() {
            return String.valueOf(this.value);
        }

        public static CategoryEnum fromValue(String value) {
            CategoryEnum[] var1 = values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                CategoryEnum b = var1[var3];
                if (b.value.equals(value)) {
                    return b;
                }
            }

            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }

        public static class Adapter extends TypeAdapter<CategoryEnum> {
            public Adapter() {
            }

            public void write(JsonWriter jsonWriter, CategoryEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            public CategoryEnum read(JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return QueryEvent.CategoryEnum.fromValue(value);
            }
        }
    }
}
