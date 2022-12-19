package whu.vbs;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import whu.vbs.Entity.CsvFile.VideoDescriptionVector;
import whu.vbs.Mapper.VideoDescriptionVectorMapper;
import whu.vbs.utils.VectorUtil;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class csvTest {
    @Test
    void readTest(){

        List<String> texts = new ArrayList<>();
        //指定路径和编码
        CsvWriter writer = CsvUtil.getWriter("C:\\Users\\Lunr\\Desktop\\texts.csv", CharsetUtil.CHARSET_UTF_8);

        for (int i = 1; i <= 7475; i++){
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


    @Test
    void VDVTest(){
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
            if (list.size() == 257){
                list.remove(256);
            }
            videoDescriptionVector.setVector(list.toString());
            videoDescriptionVectorMapper.insert(videoDescriptionVector);

            result.set(i, videoDescriptionVector);

        }
    }
}
