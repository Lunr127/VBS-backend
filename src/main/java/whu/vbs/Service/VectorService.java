package whu.vbs.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import whu.vbs.Entity.VectorResult;
import whu.vbs.Mapper.VectorMapper;
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
        VectorUtil.normalization(scoreList);

        //将（路径，得分）的键值对按得分降序
        Map<String, Double> sortMap = VectorUtil.sortMapByValues(scoreMap);

        //将路径存入urlList
        for (String path : sortMap.keySet()) {
            path = "/" + path;
            urlList.add(path);
        }

        return urlList;
    }


    public List<String> positiveFeedBack(int id) {
        List<String> urlList = new ArrayList<>();//查询结果的路径
        Map<String, Double> feedBackScoreMap = new HashMap<>();//（路径，得分）反馈键值对

        VectorResult positiveFeedBackVectorResult = vectorMapper.selectById(id);
        List<Double> vectorListDouble = VectorUtil.strToDouble(positiveFeedBackVectorResult.getVector(), 1);
        String vectorListStr = vectorListDouble.toString();
        String vectorList = vectorListStr.substring(1, vectorListStr.length() - 1);


        StringBuilder strQueryVector = new StringBuilder();
        List<Double> feedBackVector = new ArrayList<>();

        //将反馈图片的概率得分 vectorCosineSimilarity 写入文件 cosineSimilarity.txt
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("E:\\Git\\towhee-main\\TextVector\\cosineSimilarity.txt"));
            Double vectorCosineSimilarity = scoreMap.get(positiveFeedBackVectorResult.getPath());
            out.write(vectorCosineSimilarity.toString());
            out.close();
        } catch (IOException ignored) {
        }

        //将反馈图片的特征向量 vectorList 写入文件 checkVector.txt
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("E:\\Git\\towhee-main\\TextVector\\checkVector.txt"));
            out.write(vectorList);
            out.close();
        } catch (IOException ignored) {
        }

        //调用 python 函数得到新的查询向量
        runPython("python E:\\Git\\towhee-main\\qir.py");

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
        for (String path : sortMap.keySet()) {
            path = "/" + path;
            urlList.add(path);
        }

        return urlList;

    }


    public List<Double> getTextVector(String query) {
        writeQueryText(query);
        runPython("python E:\\Git\\towhee-main\\getTextVector.py");
        return readQueryVector();
    }

    public void writeQueryText(String query) {
        //将查询文本 query 写入文件 queryText.txt
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("E:\\Git\\towhee-main\\TextVector\\queryText.txt"));
            out.write(query);
            out.close();
        } catch (IOException ignored) {
        }
    }

    public List<Double> readQueryVector() {
        StringBuilder strQueryVector = new StringBuilder();
        List<Double> queryVector = new ArrayList<>();
        //读取特征向量文件

        try {
            BufferedReader br = new BufferedReader(new FileReader("E:\\Git\\towhee-main\\TextVector\\textVector.txt"));
            String st;
            while ((st = br.readLine()) != null) {
                strQueryVector.append(st);
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        //将特征向量转化为浮点数组
        queryVector = VectorUtil.strToDouble(String.valueOf(strQueryVector), 1);
        return queryVector;
    }

    public void runPython(String command) {
        //调用 python 函数得到查询文本的特征向量
        try {
            //执行 py 文件
            Process proc = Runtime.getRuntime().exec(command);
            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("exec " + command + " successfully");
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
}
