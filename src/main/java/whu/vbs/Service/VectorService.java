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


    @Autowired
    VectorMapper vectorMapper;

    public List<String> searchByText(String query) {
        List<String> urlList = new ArrayList<>();//查询结果的路径
        Map<String, Double> scoreMap = new HashMap<>();//（路径，得分）键值对
        List<VectorResult> vectorResultList = vectorMapper.selectList(null);//所有在库图片

        //得到查询文本的特征向量
        List<Double> queryVectorList = getTextVector(query);


        for (VectorResult vectorResult : vectorResultList) {
            //取出在库图片的特征向量，并转成浮点数组
            List<Double> vectorDoubleList = VectorUtil.strToDouble(vectorResult.getVector(), 1);

            //计算查询文本和图片的相似度得分
            Double cosineSimilarity = VectorUtil.getCosineSimilarity(queryVectorList, vectorDoubleList);

            //建立（路径，得分）的键值对
            scoreMap.put(vectorResult.getPath(), cosineSimilarity);
        }

        //将（路径，得分）的键值对按得分降序
        Map<String, Double> sortMap = VectorUtil.sortMapByValues(scoreMap);

        //将路径存入urlList
        for (String path : sortMap.keySet()) {
            path = "/" + path;
            urlList.add(path);
        }

        return urlList;
    }

    public List<Double> getTextVector(String query) {
        writeQueryText(query);
        runPython();
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

    public void runPython() {
        //调用 python 函数得到查询文本的特征向量
        try {
            //执行 py 文件
            Process proc = Runtime.getRuntime().exec("python E:\\Git\\towhee-main\\getTextVector.py");
            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
