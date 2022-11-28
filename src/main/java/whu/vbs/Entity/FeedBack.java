package whu.vbs.Entity;

import lombok.Data;

@Data
public class FeedBack {
    private Integer id;
    private Integer query;
    private String shot;
    private String vector;
    private Double cos;
}
