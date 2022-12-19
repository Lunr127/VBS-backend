package whu.vbs.Entity.CsvFile;

import lombok.Data;

@Data
public class MasterShotBoundary {
    String videoId;
    String startFrame;
    String startTime;
    String endFrame;
    String endTime;
}
