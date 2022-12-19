package whu.vbs.Entity.CsvFile;

import lombok.Data;

@Data
public class GrandTruth {
    private Integer id;
    private Integer query;
    private Integer junk;
    private String shot;
    private Integer stratum;
    private Integer judgment;
}
