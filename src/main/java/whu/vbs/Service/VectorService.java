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
    Map<String, Double> scoreMap = new HashMap<>();//（路径，得分）键值对
    Map<String, List<Double>> pathMap = new HashMap<>();//（路径，向量）键值对

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


    public List<String> reRank(List<String> LikePaths, List<String> NotLikePaths){
        List<String> urlList = new ArrayList<>();

        positiveFeedBack(LikePaths);

        //（路径，得分）键值对 得分归一化 并按得分降序
        Map<String, Double> sortMap = mapNormAndSort();

        //将路径存入urlList
        urlList.addAll(sortMap.keySet());

        return urlList;
    }


    public void positiveFeedBack(List<String> LikePaths) {

        //对每一个正反馈图片
        for (String path : LikePaths) {

            //得到选中的反馈图片的向量
//            String selectedVector = "";
//            for (VectorResult vectorResult : vectorResultList) {
//                if (Objects.equals(path, vectorResult.getPath())) {
//                    selectedVector = VectorUtil.strToDouble(vectorResult.getVector(), 1).toString();
//                    break;
//                }
//            }
            String selectedVector = pathMap.get(path).toString();
            selectedVector = selectedVector.substring(1, selectedVector.length()-1);

            //反馈图片的概率得分 vectorCosineSimilarity
            Double selectedCos = scoreMap.get(path);

            //调用 python 函数得到新的查询向量
            String[] args1 = new String[] { "E:\\Git\\lavis2\\venv\\Scripts\\python.exe", "E:\\Git\\lavis2\\qir.py", selectedVector, selectedCos.toString() };
            String strQueryVector = runPython(args1);
            List<Double> newQueryVector = VectorUtil.strToDouble(String.valueOf(strQueryVector),2);

            //更新所有图片的概率得分
            reRankByNewQuery(newQueryVector);
        }
    }


    public void reRankByNewQuery(List<Double> queryVector) {

        for (String path: pathMap.keySet()) {

            //取出在库图片的特征向量，并转成浮点数组
//            List<Double> vectorDoubleList = VectorUtil.strToDouble(vectorResult.getVector(), 1);
            List<Double> vectorDoubleList = pathMap.get(path);

            //计算查询文本和图片的相似度得分
            Double cosineSimilarity = VectorUtil.getCosineSimilarity(queryVector, vectorDoubleList);

            //原先得分
            Double preCos = scoreMap.get(path);

            //更新得分
            scoreMap.replace(path, preCos + cosineSimilarity);
        }
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


    public Map<String, Double> mapNormAndSort(){
        //（路径，得分）键值对 得分归一化
        VectorUtil.mapNormalization(scoreMap);

        //将（路径，得分）的键值对按得分降序
        return VectorUtil.sortMapByValues(scoreMap);
    }
}
