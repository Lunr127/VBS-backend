package whu.vbs;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import whu.vbs.Entity.VectorResult;
import whu.vbs.utils.PathUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

@SpringBootTest
public class strTests {


    @Test
    void strTest() {
        String s = "/img/shot00017_82_RKF.d7f59eba.png";
        String substring = s.substring(5);
        String root = substring.substring(4, 9);

        String[] split = substring.split("\\.");
        String path = root + '\\' + split[0] + '.' + split[2];

        System.out.println(substring);
        System.out.println(path);
    }

    @Test
    void test1() {
        String s = "/00020/shot00020_9_RKF.png";
        String substring = s.substring(7);
        String[] split = substring.split("_");
        String path = split[0] + '_' + split[1];

        System.out.println(path);
    }


    @Test
    void test2() {
        List<String> pathList = new ArrayList<>();
        pathList.add("/00019/shot00019_9_RKF.png");
        pathList.add("/00018/shot00018_9_RKF.png");
        pathList.add("/00017/shot00017_9_RKF.png");
        pathList.add("/00020/shot00020_9_RKF.png");

        pathList.replaceAll(PathUtils::handleToGTPath);

        System.out.println(pathList);

    }


    @Test
    void test3() {
        CsvReader reader = CsvUtil.getReader();
        String csvPath = "D:\\Download\\VBSDataset\\datacsv\\vector_result.csv";
        List<VectorResult> result = reader.read(ResourceUtil.getUtf8Reader(csvPath), VectorResult.class);
        System.out.println("load successfully");
        System.out.println(result.get(0));
    }

    @Test
    void test4() throws IOException {
        String shot = "./05062/shot05062_133_RKF.png";
        int index = shot.indexOf("_", 18);
        shot = shot.substring(8, index);
        System.out.println(shot);
        String path = "F:\\VBSDataset\\V3C1\\thumbnails\\" + shot.substring(4, 9) + "\\" + shot + ".png" ;
        File file = new File(path);
        InputStream in = Files.newInputStream(file.toPath());
        byte[] data = null;
        try {
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Base64.Encoder encoder = Base64.getEncoder();
        System.out.println(encoder.encodeToString(data));
    }
}
