package whu.vbs.Entity;

import lombok.Data;

@Data
public class VectorResult {
    private Integer id;
    private String videoId;
    private String path;
    private String vector;
}
