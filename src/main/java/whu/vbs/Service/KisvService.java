package whu.vbs.Service;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import whu.vbs.Entity.CsvFile.GrandTruthResult;
import whu.vbs.Entity.CsvFile.KisvQuery;
import whu.vbs.Entity.VectorResult;
import whu.vbs.Mapper.KisvQueryMapper;
import whu.vbs.Mapper.VectorMapper;
import whu.vbs.utils.PathUtils;
import whu.vbs.utils.VectorUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KisvService {

    @Autowired
    KisvQueryMapper kisvQueryMapper;

    @Autowired
    VectorMapper vectorMapper;


    Map<String, Double> scoreMap = new HashMap<>();//（路径，得分）键值对
    Map<String, Double> videoScoreMap = new HashMap<>();//（路径，得分）键值对

    List<Double> queryVector = new ArrayList<>();

    int query = 1;

    int showTopK = 500;

    public List<Map<String, String>> getInitTopK(int queryId) {

        scoreMap = new HashMap<>();
        query = queryId;

        CsvReader reader = CsvUtil.getReader();
        String csvPath = "D:\\Download\\VBSDataset\\kisv_top10000_1\\" + query + ".csv";
        List<GrandTruthResult> resultList = reader.read(ResourceUtil.getUtf8Reader(csvPath), GrandTruthResult.class);


        // 获取数据库中查询对应的特征向量
        QueryWrapper<KisvQuery> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("query_id", query);
        KisvQuery kisvQuery = kisvQueryMapper.selectOne(queryWrapper);

        queryVector = VectorUtil.strToDouble(kisvQuery.getVector(), 1);

        for (GrandTruthResult grandTruthResult : resultList) {
            //特征向量，并转成浮点数组
            List<Double> vectorDoubleList = VectorUtil.strToDouble(grandTruthResult.getVector(), 1);

            //计算查询和图片的相似度得分
            Double cosineSimilarity = VectorUtil.getCosineSimilarity(queryVector, vectorDoubleList);

            //建立（路径，得分）的键值对
            scoreMap.put(PathUtils.handleToGTPath(grandTruthResult.getShot()), cosineSimilarity);
        }

        //得分归一化
        VectorUtil.mapNormalization(scoreMap);

        //将（路径，得分）的键值对按得分降序
        Map<String, Double> sortMap = VectorUtil.sortMapByValues(scoreMap);

        List<String> urlList = new ArrayList<>();//查询结果的路径
        //将路径存入urlList
        PathUtils.savePathToUrlList(urlList, sortMap);

        return PathUtils.urlToBase64List(urlList, showTopK);
    }


    public List<Map<String, String>> showVideoByShot(String shot) {

        videoScoreMap = new HashMap<>();

        String videoId = shot.substring(4, 9);

        QueryWrapper<VectorResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("video_id", videoId);
        List<VectorResult> vectorResultList = vectorMapper.selectList(queryWrapper);

        for (VectorResult vectorResult : vectorResultList) {
            String path = vectorResult.getPath();
            List<Double> vectorDoubleList = VectorUtil.strToDouble(vectorResult.getVector(), 1);

            //计算查询和图片的相似度得分
            Double cosineSimilarity = VectorUtil.getCosineSimilarity(queryVector, vectorDoubleList);

            videoScoreMap.put(path, cosineSimilarity);
        }

        VectorUtil.mapNormalization(videoScoreMap);

        //将（路径，得分）的键值对按得分降序
        Map<String, Double> sortMap = VectorUtil.sortMapByValues(videoScoreMap);

        List<String> urlList = new ArrayList<>();//查询结果的路径
        //将路径存入urlList
        PathUtils.savePathToUrlList(urlList, sortMap);

        showTopK = Math.min(urlList.size(), 500);

        return PathUtils.urlToBase64List(urlList, showTopK);
    }

}
