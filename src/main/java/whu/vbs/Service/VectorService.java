package whu.vbs.Service;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import whu.vbs.Entity.GrandTruth;
import whu.vbs.Entity.GrandTruthResult;
import whu.vbs.Entity.Query;
import whu.vbs.Entity.VectorResult;
import whu.vbs.Mapper.GrandTruthMapper;
import whu.vbs.Mapper.QueryMapper;
import whu.vbs.Mapper.VectorMapper;
import whu.vbs.utils.PathUtils;
import whu.vbs.utils.VectorUtil;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

@Service
public class VectorService {
    Map<String, Double> scoreMap = new HashMap<>();//（路径，得分）键值对
    Map<String, List<Double>> pathMap = new HashMap<>();//（路径，向量）键值对

    int topK = 50;

    @Autowired
    VectorMapper vectorMapper;

    @Autowired
    GrandTruthMapper grandTruthMapper;

    public List<String> searchByText(String query) {
        List<String> urlList = new ArrayList<>();//查询结果的路径
        List<VectorResult> vectorResultList = new ArrayList<>();//所有在库图片

        vectorResultList = vectorMapper.selectList(null);//所有在库图片

        //得到查询文本的特征向量
        List<Double> queryVectorList = getTextVector(query);


        for (VectorResult vectorResult : vectorResultList) {
            //取出在库图片的特征向量，并转成浮点数组
            List<Double> vectorDoubleList = VectorUtil.strToDouble(vectorResult.getVector(), 1);

            //计算查询文本和图片的相似度得分
            Double cosineSimilarity = VectorUtil.getCosineSimilarity(queryVectorList, vectorDoubleList);

            //建立（路径，得分）的键值对
            scoreMap.put(vectorResult.getPath(), cosineSimilarity);

            //建立（路径，得分）的键值对
            pathMap.put(vectorResult.getPath(), vectorDoubleList);
        }

        //（路径，得分）键值对 得分归一化 并按得分降序
        Map<String, Double> sortMap = mapNormAndSort();

        //将路径存入urlList
        urlList.addAll(sortMap.keySet());

        return urlList;
    }


    public List<Map<String, String>> reRank(List<String> LikePaths, List<String> NotLikePaths){
        List<String> urlList = new ArrayList<>();

        feedBack(LikePaths, 0);
        feedBack(NotLikePaths, 1);

        //（路径，得分）键值对 得分归一化 并按得分降序
        Map<String, Double> sortMap = mapNormAndSort();

        //将路径存入urlList
        urlList.addAll(sortMap.keySet());

        List<String> topList = urlList.subList(0, topK);

        List<Map<String, String>> base64List = new ArrayList<>();
        for (String shot : topList) {
            Map<String, String> base64Map = new HashMap<>();// (base64，路径)键值对
            String base64 = "data:image/png;base64,"+ imgToBase64(shot);
            base64Map.put("shot", shot);
            base64Map.put("base64", base64);
            base64List.add(base64Map);
        }
        return base64List;
    }


    public void feedBack(List<String> Paths, int bool) {

        if (Paths.get(0).length() < 5){
            return;
        }

        //对每一个反馈图片
        for (String path : Paths) {

            System.out.println(path);

            //得到选中的反馈图片的向量
            String selectedVector = pathMap.get(path).toString();
            selectedVector = selectedVector.substring(1, selectedVector.length()-1);

            //反馈图片的概率得分 vectorCosineSimilarity
            Double selectedCos = scoreMap.get(path);

            //调用 python 函数得到新的查询向量
            String[] args1 = new String[] { "E:\\Git\\lavis2\\venv\\Scripts\\python.exe", "E:\\Git\\lavis2\\qir.py", selectedVector, selectedCos.toString() };
            String strQueryVector = runPython(args1);
            List<Double> newQueryVector = VectorUtil.strToDouble(String.valueOf(strQueryVector),2);

            //更新所有图片的概率得分
            reRankByNewQuery(newQueryVector, bool);
        }
    }


    public void reRankByNewQuery(List<Double> queryVector, int bool) {

        for (String path: pathMap.keySet()) {

            //取出在库图片的特征向量，并转成浮点数组
            List<Double> vectorDoubleList = pathMap.get(path);

            //计算查询文本和图片的相似度得分
            Double cosineSimilarity = VectorUtil.getCosineSimilarity(queryVector, vectorDoubleList);

            //原先得分
            Double preCos = scoreMap.get(path);

            //更新得分
            if (bool == 0){
                scoreMap.replace(path, preCos + 0.2 * cosineSimilarity);
            }
            else if (bool == 1){
                scoreMap.replace(path, preCos - 0.2 * cosineSimilarity);
            }

        }
    }


    public List<Double> getTextVector(String query) {
        List<Double> queryVector;

        //调用 python 函数得到查询文本的特征向量
        String[] args1 = new String[] { "E:\\Git\\lavis2\\venv\\Scripts\\python.exe", "E:\\Git\\lavis2\\textExtractor.py", query };
        String strQueryVector = runPython(args1);

        //将特征向量转化为浮点数组
        queryVector = VectorUtil.queryStrToDouble(String.valueOf(strQueryVector));

        return queryVector;
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


    public Map<String, Double> mapNormAndSort(){
        //（路径，得分）键值对 得分归一化
        VectorUtil.mapNormalization(scoreMap);

        //将（路径，得分）的键值对按得分降序
        return VectorUtil.sortMapByValues(scoreMap);
    }


    @Autowired
    QueryMapper queryMapper;

    public List<Map<String, String>> topKTest(String queryStr){
        int query = 1680;
        if (queryStr.equals("a hang glider floating in the sky on a sunny day")){
            query = 1661;
        } else if (queryStr.equals("a woman wearing sleeveless top")) {
            query = 1662;
        }
        System.out.println(query);


        String queryNumber = Integer.toString(query).substring(1);
        CsvReader reader = CsvUtil.getReader();
        String csvPath = "D:\\Download\\VBSDataset\\grand_truth\\" + queryNumber + ".csv";
        List<GrandTruthResult> result = reader.read(ResourceUtil.getUtf8Reader(csvPath), GrandTruthResult.class);

//        Map<String, Double> scoreMap = new HashMap<>();//（路径，得分）键值对
        List<String> pathList = new ArrayList<>();
        List<String> urlList = new ArrayList<>();//查询结果的路径

        Query queryVector = queryMapper.selectById(query - 1000);
        List<Double> queryVectorList = VectorUtil.strToDouble(queryVector.getVector(), 1);


        for (GrandTruthResult grandTruthResult : result) {
            pathList.add(grandTruthResult.getShot());

            //特征向量，并转成浮点数组
            List<Double> vectorDoubleList = VectorUtil.strToDouble(grandTruthResult.getVector(), 1);

            //计算查询文本和图片的相似度得分
            Double cosineSimilarity = VectorUtil.getCosineSimilarity(queryVectorList, vectorDoubleList);

            //建立（路径，得分）的键值对
            scoreMap.put(PathUtils.handleToGTPath(grandTruthResult.getShot()), cosineSimilarity);

            //建立（路径，得分）的键值对
            pathMap.put(PathUtils.handleToGTPath(grandTruthResult.getShot()), vectorDoubleList);
        }
        //将（路径，得分）的键值对按得分降序
        Map<String, Double> sortMap = VectorUtil.sortMapByValues(scoreMap);


        //将路径存入urlList
        savePathToUrlList(urlList, sortMap);

        List<String> topList = urlList.subList(0, topK);
//        List<String> base64List = new ArrayList<>();
        List<Map<String, String>> base64List = new ArrayList<>();
        for (String shot : topList) {
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
//        int index = shot.indexOf("_", 18);
//        shot = shot.substring(8, index);
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


    public int getGrandTruth(List<String> topList, int query){

        topList.replaceAll(PathUtils::handleToGTPath);
        Collections.sort(topList);

        List<GrandTruth> grandTruths = grandTruthMapper.selectList(null);
        List<GrandTruth> queryGrandTruths = new ArrayList<>();

        for (GrandTruth grandTruth : grandTruths) {
            if (grandTruth.getQuery() == query) {
                queryGrandTruths.add(grandTruth);
            } else if (grandTruth.getQuery() > query) {
                break;
            }
        }

        int number = queryGrandTruths.size();
        System.out.println("query " + query + " total true count = " + number);

        int count = 0;

        for (GrandTruth queryGrandTruth : queryGrandTruths) {
            for (String s : topList) {
                if (Objects.equals(queryGrandTruth.getShot(), s)) {
                    count++;
                } else if (queryGrandTruth.getShot().compareTo(s) < 0) {
                    break;
                }
            }
        }

        return count;
    }
}
