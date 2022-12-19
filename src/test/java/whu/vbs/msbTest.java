package whu.vbs;

import cn.hutool.core.io.FileUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import whu.vbs.Entity.CsvFile.MasterShotBoundary;
import whu.vbs.Mapper.MasterShotBoundaryMapper;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class msbTest {

    @Autowired
    MasterShotBoundaryMapper msbMapper;

    @Test
    void msbFileReadTest() {
        try {
            // jdbc 连接信息: 注: 现在版本的JDBC不需要配置driver，因为不需要Class.forName手动加载驱动
            // 建立连接
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/vbs?serverTimezone=UTC&useSSL=false", "root", "root");
            Statement statement = conn.createStatement();

            for (int i = 1; i <= 17235; i++) {
                String videoId = "";
                if (i < 10) {
                    videoId = "0000" + i;
                } else if (i < 100) {
                    videoId = "000" + i;
                } else if (i < 1000) {
                    videoId = "00" + i;
                } else if (i < 10000) {
                    videoId = "0" + i;
                } else if (i < 100000) {
                    videoId = String.valueOf(i);
                }

                String tsvPath = "D:\\Download\\VBSDataset\\msb\\" + videoId + ".tsv";
                List<String> file = FileUtil.readLines(tsvPath, StandardCharsets.UTF_8);
                StringBuilder s = new StringBuilder("INSERT INTO `vbs`.`master_shot_boundary` (`video_id`, `start_frame`, `start_time`, `end_frame`, `end_time`) VALUES");

                for (int j = 1; j < file.size(); j++) {
                    String[] split = file.get(j).split("\\s+");
                    MasterShotBoundary masterShotBoundary = new MasterShotBoundary();
                    masterShotBoundary.setVideoId(videoId);
                    masterShotBoundary.setStartFrame(split[0]);
                    masterShotBoundary.setStartTime(split[1]);
                    masterShotBoundary.setEndFrame(split[2]);
                    masterShotBoundary.setEndTime(split[3]);
                    s.append("('")
                            .append(masterShotBoundary.getVideoId()).append("','")
                            .append(masterShotBoundary.getStartFrame()).append("','")
                            .append(masterShotBoundary.getStartTime()).append("','")
                            .append(masterShotBoundary.getEndFrame()).append("','")
                            .append(masterShotBoundary.getEndTime())
                            .append("'),");
                }
                int index = s.lastIndexOf(",");
                String sql = s.substring(0, index);
                try {
                    statement.execute(sql);
                    // 若成功，打印提示信息
                    System.out.println("----------" + videoId + " success------------");
                } catch (SQLException e) {
                    System.out.println("----------" + videoId + " not success------------");
                }
            }
            // 关闭连接
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Test
    void msbGTTest() {
        String shot = "shot00136_217";
        String videoId = shot.substring(4, 9);
        int shotId = Integer.parseInt(shot.substring(10)) - 1;
        System.out.println(shotId);
        Map<String, Object> map = new HashMap<>();
        map.put("video_id", videoId);//条件1
        List<MasterShotBoundary> msbByVideoId = msbMapper.selectByMap(map);

        Double startTime = Double.parseDouble(msbByVideoId.get(shotId).getStartTime()) * 1000;
        Double endTime = Double.parseDouble(msbByVideoId.get(shotId).getEndTime()) * 1000;

        System.out.println(startTime);
        System.out.println(endTime);


    }

}
