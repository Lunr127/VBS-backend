package whu.vbs;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import whu.vbs.Entity.CsvFile.MarineFrameBoundary;
import whu.vbs.Entity.CsvFile.MarineVector;
import whu.vbs.Mapper.MarineFrameBoundaryMapper;
import whu.vbs.Mapper.MarineVectorMapper;
import whu.vbs.utils.PathUtils;
import whu.vbs.utils.VectorUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@SpringBootTest
public class marineTest {

    @Autowired
    MarineVectorMapper marineVectorMapper;

    @Autowired
    MarineFrameBoundaryMapper marineFrameBoundaryMapper;

    Map<String, List<Double>> marineVectorMap = new HashMap<>(); // (关键帧编号，向量键值对) 形式如下(Ambon_Apr2012_0001_1, vector)


    Map<String, Double> scoreMap = new HashMap<>();//（路径，得分）键值对: 形如（shot00001_1, 0.8）
    Map<String, List<Double>> pathMap = new HashMap<>();//（路径，向量）键值对: 形如（shot00001_1, [0.8,...,0.6]）
    List<String> pathList = new ArrayList<>();


    @Test
    void kisvTest() {

        // 每次查询重新初始化
        scoreMap = new HashMap<>();
        pathMap = new HashMap<>();
        pathList = new ArrayList<>();

        // 潜水数据集边界文件
        List<MarineFrameBoundary> marineFrameBoundaryList = marineFrameBoundaryMapper.selectList(null);

        // 获得数据库中marine_vector表所有内容
        List<MarineVector> marineVectors = marineVectorMapper.selectList(null);
        for (MarineVector marineVector : marineVectors) {
            // 转化id的表示形式 Ambon_Apr2012/0001/1.png ---> Ambon_Apr2012_0001_1
            String id = PathUtils.marinePathIdToFrameId(marineVector.getId());

            // 获得vector并转化成数组
            String strVector = marineVector.getVector();
            List<Double> vector = VectorUtil.marineStrToDouble(strVector);

            // 放入键值对中
            marineVectorMap.put(id, vector);
        }

        // 得到测试图片的向量
        String imagePath = "C:/Users/Lunr/Desktop/marine_image/test2.png";
        List<Double> imageVector = getImageVector(imagePath);

        // 对每个在库帧 得到与查询图片的特征相似度
        for (String shot : marineVectorMap.keySet()) {
            pathList.add(shot);

            List<Double> vector = marineVectorMap.get(shot);

            //计算查询和图片的相似度得分
            Double cosineSimilarity = VectorUtil.getCosineSimilarity(imageVector, vector);

            //建立（路径，得分）的键值对
            scoreMap.put(shot, cosineSimilarity);

            //建立（路径，向量）的键值对
            pathMap.put(shot, vector);
        }

        // 得分归一化
        VectorUtil.mapNormalization(scoreMap);

        // 将（路径，得分）的键值对按得分降序
        Map<String, Double> sortMap = VectorUtil.sortMapByValues(scoreMap);

        List<String> urlList = new ArrayList<>();//查询结果的路径
        //将路径存入urlList
        savePathToUrlList(urlList, sortMap);

        // 具体怎么测试还要考虑
        System.out.println(urlList.subList(0, 20));

        // 获取帧对应的时间示例
        String time = "";
        for (MarineFrameBoundary marineFrameBoundary : marineFrameBoundaryList) {
            if (Objects.equals(marineFrameBoundary.getFrameId(), urlList.get(0))) {
                time = marineFrameBoundary.getTime();
            }
        }
        System.out.println(time);

    }


    /**
     * 根据路径获得图片的特征向量 已通过测试 需要更改arg1的参数
     */
    List<Double> getImageVector(String imagePath) {
        //调用 python 函数得到图片的特征向量
        String[] args1 = new String[]{"E:\\Git\\lavis2\\venv\\Scripts\\python.exe", "E:\\Git\\lavis2\\imageExtractor.py", imagePath};
        String imageVector = runPython(args1);
        List<Double> imageVectorList = VectorUtil.imageStrToDouble(imageVector);
        return imageVectorList;
    }

    @Test
    void imageVectorTest(){
        String imagePath = "D:/Download/VBSDataset/VBS_task/kis-v/query.png";
        //调用 python 函数得到图片的特征向量
        String[] args1 = new String[]{"E:\\Git\\lavis2\\venv\\Scripts\\python.exe", "E:\\Git\\lavis2\\imageExtractor.py", imagePath};
        String imageVector = runPython(args1);
        List<Double> imageVectorList = VectorUtil.imageStrToDouble(imageVector);
        System.out.println(imageVectorList);
    }

    public String runPython(String[] args) {
        StringBuilder strVector = new StringBuilder();
        //调用 python 函数
        try {
            //执行 py 文件
            Process proc = Runtime.getRuntime().exec(args);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            //读取特征向量
            String line = null;
            while ((line = in.readLine()) != null) {
                strVector.append(line);
            }
            in.close();

            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return strVector.toString();
    }


    public void savePathToUrlList(List<String> urlList, Map<String, Double> sortMap) {
        for (String path : sortMap.keySet()) {
            urlList.add(path);
        }
    }


}
