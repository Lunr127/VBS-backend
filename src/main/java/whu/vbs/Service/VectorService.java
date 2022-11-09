package whu.vbs.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import whu.vbs.Entity.VectorResult;
import whu.vbs.Mapper.VectorMapper;
import whu.vbs.utils.VectorUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VectorService {


    @Autowired
    VectorMapper vectorMapper;

    public List<String> searchByText(String query) {
        List<String> urlList = new ArrayList<>();
        Map<String, List<Double>> vectorMap = new HashMap<>();
        Map<String, Double> scoreMap = new HashMap<>();

        List<VectorResult> vectorResultList = vectorMapper.selectList(null);

        //查询向量
        int id = 954;
        if (query.equals("a group of men sitting on bikes on the street")){
            id = 954;
        } else if (query.equals("a bride and groom are holding hands and looking at each other")) {
            id = 955;
        } else if (query.equals("a woman sitting at a laptop computer with her hand on her head")) {
            id = 956;
        } else if (query.equals("fish swimming in the ocean")) {
            id = 957;
        } else if (query.equals("a man riding a skateboard in a skate park")) {
            id = 958;
        }


        VectorResult queryResult = vectorMapper.selectById(id);
        String queryVector = queryResult.getVector();
        List<Double> queryVectorList = VectorUtil.strToFloat(queryVector);


        for (VectorResult vectorResult : vectorResultList) {
            List<Double> f = VectorUtil.strToFloat(vectorResult.getVector());
            vectorMap.put(vectorResult.getPath(), f);
            Double cosineSimilarity = VectorUtil.getCosineSimilarity(queryVectorList, f);
            scoreMap.put(vectorResult.getPath(), cosineSimilarity);
        }

        Map<String, Double> sortMap = VectorUtil.sortMapByValues(scoreMap);
        for (String path : sortMap.keySet()) {
//            System.out.println(path + " " + sortMap.get(path));
            path = path.substring(1);
//            path = "E:\\Git\\towhee-main\\V3Ctest" + path;
            urlList.add(path);
        }

        return urlList;
    }

    public List<Double> getTextVector(String s){
        StringBuilder result = new StringBuilder();
        List<Double> resultDoubleList = new ArrayList<>();
        try {
            String[] args1 = new String[] { "python", "E:\\Git\\towhee-main\\getTextVector.py", s };
            Process proc = Runtime.getRuntime().exec(args1);// 执行py文件

            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            resultDoubleList = VectorUtil.strToFloat(result.toString());
            in.close();
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(resultDoubleList);
        return resultDoubleList;
    }
}
