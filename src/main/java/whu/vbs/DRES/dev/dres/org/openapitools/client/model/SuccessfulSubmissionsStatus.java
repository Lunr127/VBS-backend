package whu.vbs.DRES.dev.dres.org.openapitools.client.model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.annotations.ApiModelProperty;

import java.io.IOException;
import java.util.Objects;

public class SuccessfulSubmissionsStatus {
    public static final String SERIALIZED_NAME_SUBMISSION = "submission";
    @SerializedName("submission")
    private SubmissionEnum submission;
    public static final String SERIALIZED_NAME_DESCRIPTION = "description";
    @SerializedName("description")
    private String description;
    public static final String SERIALIZED_NAME_STATUS = "status";
    @SerializedName("status")
    private Boolean status;

    public SuccessfulSubmissionsStatus() {
    }

    public SuccessfulSubmissionsStatus submission(SubmissionEnum submission) {
        this.submission = submission;
        return this;
    }

    @ApiModelProperty(
            required = true,
            value = ""
    )
    public SubmissionEnum getSubmission() {
        return this.submission;
    }

    public void setSubmission(SubmissionEnum submission) {
        this.submission = submission;
    }

    public SuccessfulSubmissionsStatus description(String description) {
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

    public SuccessfulSubmissionsStatus status(Boolean status) {
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
            SuccessfulSubmissionsStatus successfulSubmissionsStatus = (SuccessfulSubmissionsStatus)o;
            return Objects.equals(this.submission, successfulSubmissionsStatus.submission) && Objects.equals(this.description, successfulSubmissionsStatus.description) && Objects.equals(this.status, successfulSubmissionsStatus.status);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.submission, this.description, this.status});
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SuccessfulSubmissionsStatus {\n");
        sb.append("    submission: ").append(this.toIndentedString(this.submission)).append("\n");
        sb.append("    description: ").append(this.toIndentedString(this.description)).append("\n");
        sb.append("    status: ").append(this.toIndentedString(this.status)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }

    @JsonAdapter(SubmissionEnum.Adapter.class)
    public static enum SubmissionEnum {
        CORRECT("CORRECT"),
        WRONG("WRONG"),
        INDETERMINATE("INDETERMINATE"),
        UNDECIDABLE("UNDECIDABLE");

        private String value;

        private SubmissionEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public String toString() {
            return String.valueOf(this.value);
        }

        public static SubmissionEnum fromValue(String value) {
            SubmissionEnum[] var1 = values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                SubmissionEnum b = var1[var3];
                if (b.value.equals(value)) {
                    return b;
                }
            }

            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }

        public static class Adapter extends TypeAdapter<SubmissionEnum> {
            public Adapter() {
            }

            public void write(JsonWriter jsonWriter, SubmissionEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            public SubmissionEnum read(JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return SuccessfulSubmissionsStatus.SubmissionEnum.fromValue(value);
            }
        }
    }
}
