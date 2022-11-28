package whu.vbs.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import whu.vbs.Entity.GrandTruth;
import whu.vbs.Entity.VectorResult;
import whu.vbs.Mapper.GrandTruthMapper;
import whu.vbs.Mapper.VectorMapper;
import whu.vbs.utils.PathUtils;
import whu.vbs.utils.VectorUtil;

import java.io.*;
import java.util.*;

@Service
public class VectorService {

    List<Double> scoreList = new ArrayList<>();
    Map<String, Double> scoreMap = new HashMap<>();//（路径，得分）键值对
    List<VectorResult> vectorResultList = new ArrayList<>();//所有在库图片

    @Autowired
    VectorMapper vectorMapper;

    @Autowired
    GrandTruthMapper grandTruthMapper;

    public List<String> searchByText(String query) {
        List<String> urlList = new ArrayList<>();//查询结果的路径

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

            scoreList.add(cosineSimilarity);
        }

        //（路径，得分）键值对 得分归一化
        VectorUtil.mapNormalization(scoreMap);

        //将（路径，得分）的键值对按得分降序
        Map<String, Double> sortMap = VectorUtil.sortMapByValues(scoreMap);

        //将路径存入urlList
        savePathToUrlList(urlList, sortMap);

        return urlList;
    }


    public void positiveFeedBack(int id) {

        List<Double> newQueryVector;

        //根据 id 从数据库中得到反馈图片
        VectorResult positiveFeedBackVectorResult = vectorMapper.selectById(id);

        //得到反馈图片的特征向量
        List<Double> vectorListDouble = VectorUtil.strToDouble(positiveFeedBackVectorResult.getVector(), 1);

        //反馈图片的概率得分 vectorCosineSimilarity
        Double vectorCosineSimilarity = scoreMap.get(positiveFeedBackVectorResult.getPath());

        //调用 python 函数得到新的查询向量
        try {
            //执行 py 文件
            String[] args1 = new String[] { "E:\\Git\\towhee-main\\venv\\Scripts\\python.exe", "E:\\Git\\towhee-main\\qir.py", vectorListDouble.toString(), vectorCosineSimilarity.toString() };
            Process proc = Runtime.getRuntime().exec(args1);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            //读取特征向量文件
            StringBuilder strQueryVector = new StringBuilder();
            String line = null;
            while ((line = in.readLine()) != null) {
                strQueryVector.append(line);
            }
            in.close();

            //将特征向量转化为浮点数组
            newQueryVector = VectorUtil.strToDouble(String.valueOf(strQueryVector),1);

            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }



    }


    public List<String> reRankByNewQuery(){

        List<String> urlList = new ArrayList<>();//查询结果的路径
        Map<String, Double> feedBackScoreMap = new HashMap<>();//（路径，得分）反馈键值对

        StringBuilder strQueryVector = new StringBuilder();
        List<Double> feedBackVector = new ArrayList<>();

        try {
            //读取特征向量文件
            BufferedReader br = new BufferedReader(new FileReader("E:\\Git\\towhee-main\\TextVector\\newQueryVector.txt"));
            String st;
            while ((st = br.readLine()) != null) {
                strQueryVector.append(st);
            }
            //将特征向量转化为浮点数组
            feedBackVector = VectorUtil.strToDouble(String.valueOf(strQueryVector), 2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int index = 0;
        for (VectorResult vectorResult : vectorResultList) {
            //取出在库图片的特征向量，并转成浮点数组
            List<Double> vectorDoubleList = VectorUtil.strToDouble(vectorResult.getVector(), 1);

            //计算查询文本和图片的相似度得分
            Double cosineSimilarity = VectorUtil.getCosineSimilarity(feedBackVector, vectorDoubleList);

            scoreList.set(index, scoreList.get(index) + cosineSimilarity);

            //建立（路径，得分）的键值对
            feedBackScoreMap.put(vectorResult.getPath(), scoreList.get(index));
            index++;
        }


        //将（路径，得分）的键值对按得分降序
        Map<String, Double> sortMap = VectorUtil.sortMapByValues(feedBackScoreMap);

        //将路径存入urlList
        savePathToUrlList(urlList, sortMap);

        return urlList;
    }


    public void getGrandTruth(int query, List<String> pathList){
        pathList.replaceAll(PathUtils::handleToGTPath);

        Collections.sort(pathList);

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
        System.out.println("query " + query + " total true number = " + number);

        int count = 0;

        for (GrandTruth queryGrandTruth : queryGrandTruths) {
            for (String s : pathList) {
                if (Objects.equals(queryGrandTruth.getShot(), s)) {
                    count++;
                } else if (queryGrandTruth.getShot().compareTo(s) < 0) {
                    break;
                }
            }
        }

        System.out.println("predict true number = " + count);
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


    public int getIdByPath(String path) {
        int id = 1;
        for (VectorResult vectorResult : vectorResultList) {
            if (Objects.equals(path, vectorResult.getPath())) {
                id = vectorResult.getId();
                break;
            }
        }
        return id;
    }

    public void savePathToUrlList(List<String> urlList, Map<String, Double> sortMap){
        for (String path : sortMap.keySet()) {
            urlList.add(path);
        }
    }
}
