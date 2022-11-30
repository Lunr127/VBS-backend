package whu.vbs;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import whu.vbs.Entity.GrandTruthResult;
import whu.vbs.Entity.VectorResult;
import whu.vbs.Service.VectorService;
import whu.vbs.utils.PathUtils;
import whu.vbs.utils.VectorUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.*;

@SpringBootTest
public class strTests {


    @Test
    void strTest(){
        String s = "/img/shot00017_82_RKF.d7f59eba.png";
        String substring = s.substring(5);
        String root = substring.substring(4, 9);

        String[] split = substring.split("\\.");
        String path = root + '\\' + split[0] + '.' + split[2];

        System.out.println(substring);
        System.out.println(path);
    }

    @Test
    void test1(){
        String s = "/00020/shot00020_9_RKF.png";
        String substring = s.substring(7);
        String[] split = substring.split("_");
        String path = split[0] + '_' + split[1];

        System.out.println(path);
    }


    @Test
    void test2(){
        List<String> pathList = new ArrayList<>();
        pathList.add("/00019/shot00019_9_RKF.png");
        pathList.add("/00018/shot00018_9_RKF.png");
        pathList.add("/00017/shot00017_9_RKF.png");
        pathList.add("/00020/shot00020_9_RKF.png");

        pathList.replaceAll(PathUtils::handleToGTPath);

        System.out.println(pathList);

    }


    @Test
    void test3(){
        CsvReader reader = CsvUtil.getReader();
        String csvPath = "D:\\Download\\VBSDataset\\datacsv\\vector_result.csv";
        List<VectorResult> result = reader.read(ResourceUtil.getUtf8Reader(csvPath), VectorResult.class);
        System.out.println("load successfully");
        System.out.println(result.get(0));
    }


}
