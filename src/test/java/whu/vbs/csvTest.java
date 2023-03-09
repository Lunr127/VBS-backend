package whu.vbs;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.*;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import whu.vbs.Entity.ClassificationShots;
import whu.vbs.Entity.CsvFile.*;
import whu.vbs.Entity.VectorResult;
import whu.vbs.Mapper.*;
import whu.vbs.utils.VectorUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@SpringBootTest
public class csvTest {
    @Test
    void readTest() {

        List<String> texts = new ArrayList<>();
        //指定路径和编码
        CsvWriter writer = CsvUtil.getWriter("C:\\Users\\Lunr\\Desktop\\texts.csv", CharsetUtil.CHARSET_UTF_8);

        for (int i = 1; i <= 7475; i++) {
            String fileName = "";
            if (i < 10) {
                fileName = "0000" + i;
            } else if (i < 100) {
                fileName = "000" + i;
            } else if (i < 1000) {
                fileName = "00" + i;
            } else if (i < 10000) {
                fileName = "0" + i;
            }

            FileReader fileReader = new FileReader("F:\\VBSDataset\\V3C1\\info\\" + fileName + ".json");
            String result = fileReader.readString();
            JSONObject jsonObject = JSONUtil.parseObj(result);
            String description = jsonObject.getStr("description");
            description = description
                    .replace("<p class=\"first\">", "")
                    .replace("<p>", "")
                    .replace("</p>", "")
                    .replace("<br>", "")
                    .replace("</br>", "")
                    .replace("\n", "")
                    .replace("&quot", " ")
                    .replace("\"", " ")
                    .replace("&amp", " ");
            texts.add(description);
            writer.write(
                    new String[]{fileName, description}
            );
        }


    }


    @Autowired
    VideoDescriptionVectorMapper videoDescriptionVectorMapper;

    @Autowired
    ClassificationShotsMapper classificationShotsMapper;


    @Test
    void VDVTest() {
        CsvReader reader = CsvUtil.getReader();
        String csvPath = "D:\\Download\\VBSDataset\\videoDescriptionVector.csv";
        List<VideoDescriptionVector> result = reader.read(ResourceUtil.getUtf8Reader(csvPath), VideoDescriptionVector.class);

        for (int i = 0; i < result.size(); i++) {
            int j = i + 1;
            String videoId = "";
            if (j < 10) {
                videoId = "0000" + j;
            } else if (j < 100) {
                videoId = "000" + j;
            } else if (j < 1000) {
                videoId = "00" + j;
            } else if (j < 10000) {
                videoId = "0" + j;
            } else if (j < 100000) {
                videoId = String.valueOf(j);
            }

            VideoDescriptionVector videoDescriptionVector = result.get(i);
            videoDescriptionVector.setVideoId(videoId);
            List<Double> list = VectorUtil.avsQueryStrToDouble(videoDescriptionVector.getVector());
            if (list.size() == 257) {
                list.remove(256);
            }
            videoDescriptionVector.setVector(list.toString());
            videoDescriptionVectorMapper.insert(videoDescriptionVector);

            result.set(i, videoDescriptionVector);

        }
    }

    @Test
    void CategoryTest() {
        Map<String, List<String>> map = new HashMap<>();
        CsvReader reader = CsvUtil.getReader();

        for (int i = 1; i <= 7475; i++) {
            if (i >= 4304 && i <= 4318 || i == 4350) {
                continue;
            }
            String fileName = "";
            if (i < 10) {
                fileName = "0000" + i;
            } else if (i < 100) {
                fileName = "000" + i;
            } else if (i < 1000) {
                fileName = "00" + i;
            } else if (i < 10000) {
                fileName = "0" + i;
            }
            String csvPath = "D:\\Download\\VBSDataset\\classification_csv\\" + fileName + ".csv";
            List<ClassificationResult> result = reader.read(ResourceUtil.getUtf8Reader(csvPath), ClassificationResult.class);
            for (ClassificationResult classificationResult : result) {
                String shotId = classificationResult.getShotsid();
                int begin = shotId.indexOf("s");
                int end = shotId.indexOf("_", begin + 11);

                String category = classificationResult.getCategory();
                String[] categoryList = category.split(",");
                for (String c : categoryList) {
                    if (Objects.equals(c, "")) {
                        break;
                    }
                    if (map.get(c) != null) {
                        List<String> shotList = map.get(c);
                        shotList.add(shotId.substring(begin, end));
                        map.replace(c, shotList);
                    } else {
                        List<String> shotList = new ArrayList<>();
                        shotList.add(shotId.substring(begin, end));
                        map.put(c, shotList);
                    }
                }
            }
        }
        System.out.println(map.get("vehicle"));

    }

    @Autowired
    MarineFrameBoundaryMapper marineFrameBoundaryMapper;

    @Test
    void marineTest() {
        CsvReader reader = CsvUtil.getReader();
        File file = new File("D:\\Download\\VBSDataset\\Marine_frames_csv");
        File[] files = file.listFiles();
        assert files != null;
        for (File f : files) {
            List<MarineVector> result = reader.read(ResourceUtil.getUtf8Reader(String.valueOf(f)), MarineVector.class);
            for (MarineVector marineVector : result) {
                List<Double> vector = VectorUtil.imageStrToDouble(marineVector.getVector());
                System.out.println(vector.size());
            }
            break;
        }
    }

    @Autowired
    VectorMapper vectorResultMapper;


    @Test
    void vectorTest() {
        String videoId = "00145";
        QueryWrapper<VectorResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("video_id", videoId);
        List<VectorResult> vectorResultList = vectorResultMapper.selectList(queryWrapper);

        System.out.println(vectorResultList);
    }

    @Test
    void vectorResultTest() {
        try {
            // jdbc 连接信息: 注: 现在版本的JDBC不需要配置driver，因为不需要Class.forName手动加载驱动
            // 建立连接
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/vbs?serverTimezone=UTC&useSSL=false", "root", "root");
            Statement statement = conn.createStatement();

            CsvReader reader = CsvUtil.getReader();

            List<Integer> idList = new ArrayList<>();
            idList.add(1034);
            idList.add(2797);
            idList.add(4821);
            idList.add(5709);
            idList.add(5999);
            idList.add(6847);
            idList.add(6968);
            idList.add(7434);
            idList.add(7698);
            idList.add(7929);
            idList.add(7966);
            idList.add(8003);
            idList.add(8096);
            idList.add(10932);
            idList.add(13271);
            idList.add(13460);
            idList.add(13590);
            idList.add(14145);
            idList.add(14702);
            idList.add(14791);
            idList.add(15581);


            for (int i : idList) {
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

                String csvPath = "C:\\VBSDataset\\datacsv\\" + videoId + ".csv";
                List<CsvTest> result = reader.read(ResourceUtil.getUtf8Reader(csvPath), CsvTest.class);
                StringBuilder s = new StringBuilder("INSERT INTO `vbs`.`vector_result` (`video_id`, `path`, `vector`) VALUES");

                for (CsvTest csvTest : result) {

                    VectorResult vectorResult = new VectorResult();
                    String id = csvTest.getId();
                    int begin = id.indexOf("s");
                    int end = id.indexOf("p");
                    id = id.substring(begin, end - 5);

                    vectorResult.setVideoId(id.substring(4, 9));
                    vectorResult.setPath(id);
                    vectorResult.setVector(VectorUtil.imageStrToDouble(csvTest.getVector()).toString());

                    s.append("('")
                            .append(vectorResult.getVideoId()).append("','")
                            .append(vectorResult.getPath()).append("','")
                            .append(vectorResult.getVector())
                            .append("'),");
                }

                int index = s.lastIndexOf(",");
                String sql = s.substring(0, index);
                System.out.println(sql);
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


    @Autowired
    ImageFilterMapper imageFilterMapper;
    @Test
    void imageFilter(){
        CsvReader reader = CsvUtil.getReader();
//从文件中读取CSV数据
        CsvData data = reader.read(FileUtil.file("E:\\Git\\lavis2\\imageFilter\\info.csv"));
        List<CsvRow> rows = data.getRows();
//遍历行
        for (CsvRow csvRow : rows) {
            //getRawList返回一个List列表，列表的每一项为CSV中的一个单元格（既逗号分隔部分）
            String shot = csvRow.getRawList().get(0);
            int begin = shot.indexOf("_");
            int end = shot.indexOf(".");
            shot = shot.substring(begin - 9, end);
            System.out.println(shot);
            ImageFilter imageFilter = new ImageFilter();
            imageFilter.setShot(shot);
            imageFilterMapper.insert(imageFilter);
        }
    }

}
