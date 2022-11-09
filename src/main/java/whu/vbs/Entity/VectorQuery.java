package whu.vbs.Entity;

import lombok.Data;

@Data
public class VectorQuery {
    private Integer id;
    private String query;
    private String vector;
}
