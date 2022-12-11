package whu.vbs;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import whu.vbs.Entity.*;
import whu.vbs.Mapper.AvsGrandTruthMapper;
import whu.vbs.Mapper.AvsQueryMapper;
import whu.vbs.Mapper.GrandTruthMapper;
import whu.vbs.Mapper.MasterShotBoundaryMapper;
import whu.vbs.utils.PathUtils;
import whu.vbs.utils.VectorUtil;

import java.nio.charset.StandardCharsets;
import java.util.*;

@SpringBootTest
public class avsTest {

    @Autowired
    AvsGrandTruthMapper avsGrandTruthMapper;

    @Autowired
    GrandTruthMapper grandTruthMapper;

    @Autowired
    MasterShotBoundaryMapper msbMapper;

    @Autowired
    AvsQueryMapper avsQueryMapper;

    Map<String, Double> scoreMap = new HashMap<>();//（路径，得分）键值对
    Map<String, List<Double>> vectorMap = new HashMap<>();//（路径，向量）键值对
    List<String> pathList = new ArrayList<>();

    Map<String, List<MasterShotBoundary>> msbMap = new HashMap<>();

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

        for (int i = avsGrandTruths.size() - 1; i >= 0; i--) {
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

    @Test
    void initialSortingTest() {
        int topK = 1000;
        int total = 0;

        int query;
        int count;

        List<MasterShotBoundary> masterShotBoundaryList = msbMapper.selectList(null);

        int breakCount = 0;
        for (int index = 0; index < masterShotBoundaryList.size(); index++){
            for (int i = 1; i <= 7475; i++) {
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

                List<MasterShotBoundary> msbByVideoId = new ArrayList<>();
                while (Objects.equals(masterShotBoundaryList.get(index).getVideoId(), videoId)){
                    msbByVideoId.add(masterShotBoundaryList.get(index));
                    index++;
                }
                msbMap.put(videoId, msbByVideoId);
                breakCount = i;
            }
            if (breakCount == 7475){
                break;
            }
        }

        for (query = 1; query <= 10; query++) {

            if (query == 3 || query == 8 || query == 7){
                continue;
            }

            scoreMap = new HashMap<>();
            vectorMap = new HashMap<>();
            pathList = new ArrayList<>();

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

                //建立（路径，概率）的键值对
                vectorMap.put(PathUtils.handleToGTPath(grandTruthResult.getShot()), vectorDoubleList);
            }

            //initial
            VectorUtil.mapNormalization(scoreMap);

            //将（路径，得分）的键值对按得分降序
            Map<String, Double> sortMap = VectorUtil.sortMapByValues(scoreMap);

            List<String> urlList = new ArrayList<>();//查询结果的路径
            //将路径存入urlList
            savePathToUrlList(urlList, sortMap);
            List<String> topList = urlList.subList(0, topK);

            count = getGTMatch(topList, query);

            System.out.println("top K = " + topK);
            System.out.println("predict true count = " + count);
            System.out.println("precision@" + topK + " = " + ((double) count / topK));
            System.out.println();
        }
    }


    public void savePathToUrlList(List<String> urlList, Map<String, Double> sortMap) {
        for (String path : sortMap.keySet()) {
            urlList.add(path);
        }
    }

    int getGTMatch(List<String> topList, int query) {
        Collections.sort(topList);

        Map<String, Object> selectByQueryMap = new HashMap<>();
        selectByQueryMap.put("query_id", query);
        List<AvsGrandTruth> avsGrandTruths = avsGrandTruthMapper.selectByMap(selectByQueryMap);

        List<AvsGrandTruth> queryGrandTruths = new ArrayList<>();

        for (AvsGrandTruth avsGrandTruth : avsGrandTruths) {
            if (Integer.parseInt(avsGrandTruth.getVideoId()) < 7476) {
                queryGrandTruths.add(avsGrandTruth);
            }
        }

        int number = queryGrandTruths.size();
        System.out.println("query " + query + " total true count = " + number);

        int count = 0;

        Set<AvsGrandTruth> avsGrandTruthSet = new HashSet<>();
        for (String shot : topList) {
            String videoId = shot.substring(4, 9);
            int shotId = Integer.parseInt(shot.substring(10)) - 1;
            if (shotId > 100){
                shotId -= 1;
            }
            List<MasterShotBoundary> msbByVideoId = msbMap.get(videoId);
            double startTime = Double.parseDouble(msbByVideoId.get(shotId).getStartTime()) * 1000;
            double endTime = Double.parseDouble(msbByVideoId.get(shotId).getEndTime()) * 1000;


            for (AvsGrandTruth avsGrandTruth : queryGrandTruths) {
                double gtStartTime = Double.parseDouble(avsGrandTruth.getStartTime());
                double gtEndTime = Double.parseDouble(avsGrandTruth.getEndTime());
                if (Objects.equals(avsGrandTruth.getVideoId(), videoId) && gtStartTime > startTime - 10000 && gtEndTime < endTime + 10000) {
                    avsGrandTruthSet.add(avsGrandTruth);
                }
            }
        }
        count = avsGrandTruthSet.size();

        return count;
    }


}
