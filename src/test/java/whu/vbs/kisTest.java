package whu.vbs;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import whu.vbs.Entity.CsvFile.*;
import whu.vbs.Mapper.KisvGrandTruthMapper;
import whu.vbs.Mapper.KisvQueryMapper;
import whu.vbs.Mapper.MasterShotBoundaryMapper;
import whu.vbs.utils.PathUtils;
import whu.vbs.utils.VectorUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@SpringBootTest
public class kisTest {

    @Autowired
    KisvGrandTruthMapper kisvGrandTruthMapper;

    @Autowired
    MasterShotBoundaryMapper msbMapper;

    @Autowired
    KisvQueryMapper kisvQueryMapper;



    Map<String, List<MasterShotBoundary>> msbMap = new HashMap<>();

    Map<String, Double> scoreMap = new HashMap<>();//（路径，得分）键值对: 形如（shot00001_1, 0.8）
    Map<String, List<Double>> pathMap = new HashMap<>();//（路径，向量）键值对: 形如（shot00001_1, [0.8,...,0.6]）
    List<String> pathList = new ArrayList<>();
    Map<Integer, String> queryVideoMap = new HashMap<>();// (查询编号，目标视频编号)键值对


    @Test
    void kisvTest(){
        List<KisvGrandTruth> kisvGrandTruths = kisvGrandTruthMapper.selectList(null);
        List<MasterShotBoundary> masterShotBoundaryList = msbMapper.selectList(null);

        // msb相关初始化 收起就行 不用多管
        int breakCount = 0;
        for (int index = 0; index < masterShotBoundaryList.size(); index++) {
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
                while (Objects.equals(masterShotBoundaryList.get(index).getVideoId(), videoId)) {
                    msbByVideoId.add(masterShotBoundaryList.get(index));
                    index++;
                    if (index == 2508108) {
                        break;
                    }
                }
                msbMap.put(videoId, msbByVideoId);
                breakCount = i;
            }
            if (breakCount == 7475) {
                break;
            }
        }

        List<Integer> queryList = new ArrayList<>();
        for (KisvGrandTruth kisvGrandTruth : kisvGrandTruths) {
            int videoId = Integer.parseInt(kisvGrandTruth.getVideoId());
            int queryId = kisvGrandTruth.getQueryId();
            if (videoId <= 7475){
                queryList.add(queryId);
                queryVideoMap.put(queryId, kisvGrandTruth.getVideoId());
            }
        }

        System.out.println(queryList);

        for (int query : queryList) {

            // 每次查询重新初始化
            scoreMap = new HashMap<>();
            pathMap = new HashMap<>();
            pathList = new ArrayList<>();

            // 读取faiss产生的top10000文件
            CsvReader reader = CsvUtil.getReader();
            String csvPath = "D:\\Download\\VBSDataset\\kisv_top10000\\" + query + ".csv";
            List<GrandTruthResult> result = reader.read(ResourceUtil.getUtf8Reader(csvPath), GrandTruthResult.class);

            // 获取数据库中查询语句对应的特征向量
            QueryWrapper<KisvQuery> queryWrapper=new QueryWrapper();
            queryWrapper.eq("query_id",query);
            List<KisvQuery> kisvQueryList = kisvQueryMapper.selectList(queryWrapper);
            KisvQuery kisvQuery = kisvQueryList.get(0);
            List<Double> queryVectorList = VectorUtil.strToDouble(kisvQuery.getVector(), 1);

            // gt处理 收起就好 不用管
            for (GrandTruthResult grandTruthResult : result) {
                pathList.add(PathUtils.handleToGTPath(grandTruthResult.getShot()));

                //特征向量，并转成浮点数组
                List<Double> vectorDoubleList = VectorUtil.strToDouble(grandTruthResult.getVector(), 1);

                //计算查询和图片的相似度得分
                Double cosineSimilarity = VectorUtil.getCosineSimilarity(queryVectorList, vectorDoubleList);

                //建立（路径，得分）的键值对
                scoreMap.put(PathUtils.handleToGTPath(grandTruthResult.getShot()), cosineSimilarity);

                //建立（路径，概率）的键值对
                pathMap.put(PathUtils.handleToGTPath(grandTruthResult.getShot()), vectorDoubleList);
            }

            // 得分归一化
            VectorUtil.mapNormalization(scoreMap);

            // 将（路径，得分）的键值对按得分降序
            Map<String, Double> sortMap = VectorUtil.sortMapByValues(scoreMap);

            List<String> urlList = new ArrayList<>();//查询结果的路径
            //将路径存入urlList
            savePathToUrlList(urlList, sortMap);

            System.out.println("------------- Query: " + query + "-----------");
            double score = getScore(urlList, query);
            System.out.println();
        }


    }


    public double getScore(List<String> urlList, int query) {
        QueryWrapper<KisvGrandTruth> queryWrapper=new QueryWrapper();
        queryWrapper.eq("query_id",query);
        List<KisvGrandTruth> kisvGrandTruths = kisvGrandTruthMapper.selectList(queryWrapper);
        KisvGrandTruth kisvGrandTruth = kisvGrandTruths.get(0);
        System.out.println(kisvGrandTruth);

        double gtStartTime = Double.parseDouble(kisvGrandTruth.getStartTime());
        double gtEndTime = Double.parseDouble(kisvGrandTruth.getEndTime());

        int count = 0;

        for (String shot : urlList) {
            count++;
            String videoId = shot.substring(4, 9);
            int shotId = Integer.parseInt(shot.substring(10)) - 1;

            if (!videoId.equals(kisvGrandTruth.getVideoId())){
                continue;
            }
            if (shotId > 100) {
                shotId -= 1;
            }
            List<MasterShotBoundary> msbByVideoId = msbMap.get(videoId);
            if (msbByVideoId == null) {
                continue;
            }
            double startTime = Double.parseDouble(msbByVideoId.get(shotId).getStartTime()) * 1000;
            double endTime = Double.parseDouble(msbByVideoId.get(shotId).getEndTime()) * 1000;

            if (((gtStartTime > startTime - 2000) || (gtStartTime < startTime + 2000)) &&
                    ((gtEndTime > endTime - 2000) || (gtEndTime < endTime + 2000))){
                System.out.println(shot);
                System.out.println(count);
                break;
            }

        }
        return count;
    }


    @Test
    void getImageVector(){
        String imagePath = "C:/Users/Lunr/Desktop/image/06040_1.png";


        //调用 python 函数得到图片的特征向量
        String[] args1 = new String[]{"E:\\Git\\lavis2\\venv\\Scripts\\python.exe", "E:\\Git\\lavis2\\imageExtractor.py", imagePath};
        String imageVector = runPython(args1);
        List<Double> imageVectorList = VectorUtil.imageStrToDouble(imageVector);
        System.out.println(imageVectorList);
    }

    public void savePathToUrlList(List<String> urlList, Map<String, Double> sortMap) {
        for (String path : sortMap.keySet()) {
            urlList.add(path);
        }
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

}
