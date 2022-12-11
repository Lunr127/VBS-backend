package whu.vbs.Service;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import whu.vbs.Entity.AvsQuery;
import whu.vbs.Entity.GrandTruthResult;
import whu.vbs.Entity.MasterShotBoundary;
import whu.vbs.Entity.Query;
import whu.vbs.Mapper.*;
import whu.vbs.utils.PathUtils;
import whu.vbs.utils.VectorUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

@Service
public class AvsService {

    @Autowired
    AvsGrandTruthMapper avsGrandTruthMapper;

    @Autowired
    GrandTruthMapper grandTruthMapper;

    @Autowired
    MasterShotBoundaryMapper msbMapper;

    @Autowired
    AvsQueryMapper avsQueryMapper;

    @Autowired
    VectorMapper vectorMapper;

    Map<String, Double> scoreMap = new HashMap<>();//（路径，得分）键值对
    Map<String, List<Double>> pathMap = new HashMap<>();//（路径，向量）键值对
    List<String> pathList = new ArrayList<>();

    Map<String, List<MasterShotBoundary>> msbMap = new HashMap<>();

    int topK = 1000;
    int showTopK = 500;
    int query = 1661;


    Map<Integer, String> queryMap = new HashMap<>();
    public void setQueryMap(){
        queryMap.put(1, "showing one person playing a guitar (other people but no other musicians may be visible)");
        queryMap.put(2, "one or more persons balancing on a bar, railing, rope or slackline, without any device under their feet");
        queryMap.put(4, "someone riding a horse or sitting on a horse (living animal)");
        queryMap.put(5, "taken from any vehicle driving inside a tunnel, requiring part of the vehicle being visible");
        queryMap.put(6, "outdoor shots showing a teddy bear (toy)");
        queryMap.put(7, "a waterfall, without people");
        queryMap.put(9, "one or more decorated trees (not just branches) that are not lit (inside or outside)");
        queryMap.put(10, "someone with their hands on a camera (not e.g. a phone-like device), filming or taking/preparing to take a picture");
    }

    public List<Map<String, String>> topKTest(String queryStr){

        scoreMap = new HashMap<>();
        pathMap = new HashMap<>();

        setQueryMap();
        for (Integer key: queryMap.keySet()){
            if (queryStr.equals(queryMap.get(key))) {
                query = key;
            }
        }


        CsvReader reader = CsvUtil.getReader();
        String csvPath = "D:\\Download\\VBSDataset\\avs_grand_truth\\" + query + ".csv";
        List<GrandTruthResult> result = reader.read(ResourceUtil.getUtf8Reader(csvPath), GrandTruthResult.class);


        AvsQuery queryVector = avsQueryMapper.selectById(query);

        List<Double> queryVectorList = VectorUtil.strToDouble(queryVector.getVector(), 1);


        for (GrandTruthResult grandTruthResult : result) {
            pathList.add(PathUtils.handleToGTPath(grandTruthResult.getShot()));

            //特征向量，并转成浮点数组
            List<Double> vectorDoubleList = VectorUtil.strToDouble(grandTruthResult.getVector(), 1);

            //计算查询文本和图片的相似度得分
            Double cosineSimilarity = VectorUtil.getCosineSimilarity(queryVectorList, vectorDoubleList);

            //建立（路径，得分）的键值对
            scoreMap.put(PathUtils.handleToGTPath(grandTruthResult.getShot()), cosineSimilarity);

            //建立（路径，向量）的键值对
            pathMap.put(PathUtils.handleToGTPath(grandTruthResult.getShot()), vectorDoubleList);
        }

        //initial
        VectorUtil.mapNormalization(scoreMap);

        //将（路径，得分）的键值对按得分降序
        Map<String, Double> sortMap = VectorUtil.sortMapByValues(scoreMap);

        List<String> urlList = new ArrayList<>();//查询结果的路径
        //将路径存入urlList
        savePathToUrlList(urlList, sortMap);
        List<String> topList = urlList.subList(0, topK);


        List<Map<String, String>> base64List = new ArrayList<>();
        for (String shot : topList.subList(0, showTopK)) {
            Map<String, String> base64Map = new HashMap<>();// (base64，路径)键值对
            String base64 = "data:image/png;base64,"+ imgToBase64(shot);
            base64Map.put("shot", shot);
            base64Map.put("base64", base64);
            base64List.add(base64Map);
        }
        return base64List;

    }

    public void savePathToUrlList(List<String> urlList, Map<String, Double> sortMap) {
        for (String path : sortMap.keySet()) {
            urlList.add(path);
        }
    }

    public String imgToBase64(String shot){
        String path = "F:\\VBSDataset\\V3C1\\thumbnails\\" + shot.substring(4, 9) + "\\" + shot + ".png" ;
        File file = new File(path);

        byte[] data = null;
        try {
            InputStream in = Files.newInputStream(file.toPath());
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(data);
    }

}
