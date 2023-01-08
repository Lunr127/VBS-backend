package whu.vbs.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import whu.vbs.Entity.CsvFile.MasterShotBoundary;
import whu.vbs.Mapper.MarineFrameBoundaryMapper;
import whu.vbs.Mapper.MasterShotBoundaryMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SubmitService {

    @Autowired
    MasterShotBoundaryMapper masterShotBoundaryMapper;


    public void submit(List<String> urlList) {

        Map<String, String> shotTimeMap = new HashMap<>();

        for (String shot : urlList) {
            shot = shot.substring(1, shot.length() - 1);
            String videoId = shot.substring(4, 9);
            String shotId = shot.substring(shot.indexOf("_") + 1);

            QueryWrapper<MasterShotBoundary> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("video_id", videoId);
            List<MasterShotBoundary> boundaryList = masterShotBoundaryMapper.selectList(queryWrapper);
            MasterShotBoundary boundary = boundaryList.get(Integer.parseInt(shotId) - 1);

            Double startTime = Double.parseDouble(boundary.getStartTime()) * 1000;
            Double endTime = Double.parseDouble(boundary.getEndTime()) * 1000;
            double frameTime = (startTime + endTime) / 2;

            shotTimeMap.put(shot, String.valueOf(frameTime));
        }

        System.out.println(shotTimeMap);
    }

}
