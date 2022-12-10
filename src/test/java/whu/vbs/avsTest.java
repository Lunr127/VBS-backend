package whu.vbs;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import whu.vbs.Entity.AvsGrandTruth;
import whu.vbs.Entity.CsvTest;
import whu.vbs.Entity.MasterShotBoundary;
import whu.vbs.Mapper.AvsGrandTruthMapper;
import whu.vbs.Mapper.MasterShotBoundaryMapper;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SpringBootTest
public class avsTest {

    @Autowired
    AvsGrandTruthMapper avsGrandTruthMapper;

    @Autowired
    MasterShotBoundaryMapper msbMapper;

    @Test
    void saveAvs() {
        String tsvPath = "D:\\Download\\VBSDataset\\vbs22\\AVSans.csv";
        List<String> file = FileUtil.readLines(tsvPath, StandardCharsets.UTF_8);
        System.out.println(file.toString());
        for (int i = 1; i < file.size(); i++) {
            String[] split = file.get(i).split(",");
            AvsGrandTruth avsGrandTruth = new AvsGrandTruth();
            avsGrandTruth.setQueryId(Integer.valueOf(split[0]));
            avsGrandTruth.setVideoId(split[1]);
            avsGrandTruth.setStartTime(split[2]);
            avsGrandTruth.setEndTime(split[3]);
            avsGrandTruthMapper.insert(avsGrandTruth);
        }
    }


    @Test
    void avsGTTest() {
        String shot = "shot00136_217";
        String videoId = shot.substring(4, 9);
        int shotId = Integer.parseInt(shot.substring(10)) - 1;
        System.out.println(shotId);
        Map<String, Object> selectByVideoIdMap = new HashMap<>();
        selectByVideoIdMap.put("video_id", videoId);
        List<MasterShotBoundary> msbByVideoId = msbMapper.selectByMap(selectByVideoIdMap);

        double startTime = Double.parseDouble(msbByVideoId.get(shotId).getStartTime()) * 1000;
        double endTime = Double.parseDouble(msbByVideoId.get(shotId).getEndTime()) * 1000;


        int queryId = 1;
        Map<String, Object> selectByQueryMap = new HashMap<>();
        selectByQueryMap.put("query_id", queryId);
        List<AvsGrandTruth> avsGrandTruths = avsGrandTruthMapper.selectByMap(selectByQueryMap);

        int count = 0;

        for (int i = avsGrandTruths.size()-1; i >= 0; i--){
            double gtStartTime = Double.parseDouble(avsGrandTruths.get(i).getStartTime());
            double gtEndTime = Double.parseDouble(avsGrandTruths.get(i).getEndTime());
            if (Objects.equals(avsGrandTruths.get(i).getVideoId(), videoId) && gtStartTime > startTime - 10000 && gtEndTime < endTime + 10000) {
                count++;
                System.out.println(avsGrandTruths.get(i));
                avsGrandTruths.remove(i);
            }
        }

        System.out.println(count);


    }
}
