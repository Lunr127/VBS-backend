package whu.vbs;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import whu.vbs.Entity.CsvFile.GrandTruth;
import whu.vbs.Entity.CsvFile.GrandTruthResult;
import whu.vbs.Entity.CsvFile.Query;
import whu.vbs.Mapper.GrandTruthMapper;
import whu.vbs.Mapper.QueryMapper;
import whu.vbs.utils.PathUtils;
import whu.vbs.utils.VectorUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@SpringBootTest
public class grandTruthTests {


    @Autowired
    GrandTruthMapper grandTruthMapper;

    @Autowired
    QueryMapper queryMapper;

    Map<String, Double> scoreMap = new HashMap<>();//（路径，得分）键值对
    Map<String, List<Double>> vectorMap = new HashMap<>();//（路径，向量）键值对
    List<String> pathList = new ArrayList<>();


    Map<Integer, String[]> likeShotsMap = new HashMap<>();
    Map<Integer, String[]> notLikeShotsMap = new HashMap<>();

    void setLikeShotsMap(){
        likeShotsMap.put(1661, new String[]{"shot00057_130", "shot00410_374", "shot00847_59", "shot01564_23", "shot03072_62"});
        likeShotsMap.put(1662, new String[]{"shot00015_42", "shot00058_106", "shot00479_95", "shot01604_103", "shot04054_48"});
        likeShotsMap.put(1663, new String[]{"shot00127_36", "shot01597_105", "shot02546_16", "shot02853_101", "shot02853_74"});
        likeShotsMap.put(1664, new String[]{"shot01613_162", "shot01613_2", "shot01745_92", "shot01887_53", "shot04096_397"});
        likeShotsMap.put(1665, new String[]{"shot00535_672", "shot01352_97", "shot01531_1", "shot03840_240", "shot04021_14"});
        likeShotsMap.put(1666, new String[]{"shot00143_30", "shot00278_69", "shot00378_39", "shot00167_201", "shot00143_5"});
        likeShotsMap.put(1667, new String[]{"shot00319_171", "shot00505_7", "shot00505_6", "shot00505_58", "shot00812_14"});
        likeShotsMap.put(1668, new String[]{"shot00152_132", "shot00181_139", "shot00585_314", "shot00918_192", "shot01512_54"});
        likeShotsMap.put(1669, new String[]{"shot00182_6", "shot00580_10", "shot00785_114", "shot01170_16", "shot01170_25"});
        likeShotsMap.put(1670, new String[]{"shot00007_25", "shot00114_36", "shot00562_75", "shot00573_123", "shot00007_88"});

    }

    void setNotLikeShotsMap(){
        notLikeShotsMap.put(1661, new String[]{"shot01507_151", "shot07168_10", "shot03340_131", "shot01146_55", "shot00615_98"});
        notLikeShotsMap.put(1662, new String[]{"shot00354_68", "shot00464_189", "shot00479_108", "shot01138_33", "shot03961_56"});
        notLikeShotsMap.put(1663, new String[]{"shot00342_11", "shot00342_9", "shot01380_22", "shot01743_100", "shot02768_80"});
        notLikeShotsMap.put(1664, new String[]{"shot01237_155", "shot05262_16", "shot06839_5", "shot07168_77", "shot06775_135"});
        notLikeShotsMap.put(1665, new String[]{"shot00158_50", "shot00464_14", "shot00750_635", "shot00896_139", "shot02499_118"});
        notLikeShotsMap.put(1666, new String[]{"shot00181_75", "shot00378_166", "shot00347_75", "shot00332_32", "shot00248_22"});
        notLikeShotsMap.put(1667, new String[]{"shot00015_11", "shot00028_11", "shot00036_514", "shot00036_516", "shot00036_524"});
        notLikeShotsMap.put(1668, new String[]{"shot00036_48", "shot00054_1", "shot00197_41", "shot00193_604", "shot00743_42"});
        notLikeShotsMap.put(1669, new String[]{"shot00217_187", "shot00669_51", "shot00056_119", "shot00087_129", "shot00159_86"});
        notLikeShotsMap.put(1670, new String[]{"shot00573_26", "shot00573_295", "shot00389_439", "shot00056_151", "shot00114_39"});
    }



    @Test
    void initialSortingTest() {

        setLikeShotsMap();
        setNotLikeShotsMap();

        int topK = 1000;
        int total = 0;

        int query;
        int count;

        for (query = 1661; query <= 1670; query++) {

            scoreMap = new HashMap<>();
            vectorMap = new HashMap<>();
            pathList = new ArrayList<>();

            String queryNumber = Integer.toString(query).substring(1);
            CsvReader reader = CsvUtil.getReader();
            String csvPath = "D:\\Download\\VBSDataset\\grand_truth_top10000\\" + queryNumber + ".csv";
            List<GrandTruthResult> result = reader.read(ResourceUtil.getUtf8Reader(csvPath), GrandTruthResult.class);



            Query queryVector = queryMapper.selectById(query - 1000);
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
            total += count;

            System.out.println("top K = " + topK);
            System.out.println("predict true count = " + count);
            System.out.println("precision@" + topK + " = " + ((double) count / topK));
            System.out.println();


            String[] shots = likeShotsMap.get(query);
            for (String shot : shots) {
                qmr(shot);  //qmr
                //Rocchio(shot, 0);   //Rocchio
            }

//            shots = notLikeShotsMap.get(query);
//            for (String shot : shots) {
//                //qmr(shot);  //qmr
//                Rocchio(shot, 1);   //Rocchio
//            }

            VectorUtil.mapNormalization(scoreMap);
            //将（路径，得分）的键值对按得分降序
            Map<String, Double> reRankSortMap = VectorUtil.sortMapByValues(scoreMap);


            //将路径存入urlList
            List<String> reRankUrlList = new ArrayList<>();//查询结果的路径
            savePathToUrlList(reRankUrlList, reRankSortMap);
            List<String> reRandTopList = reRankUrlList.subList(0, topK);

            count = getGTMatch(reRandTopList, query);
            total += count;

            System.out.println("top K = " + topK);
            System.out.println("predict true count = " + count);
            System.out.println("precision@" + topK + " = " + ((double) count / topK));
            System.out.println();
        }

//        System.out.println("mean precision@" + topK + " = " + ((double) total) / topK / 20);


    }

    int getGTMatch(List<String> topList, int query) {
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
                    //System.out.println(s);
                } else if (queryGrandTruth.getShot().compareTo(s) < 0) {
                    break;
                }
            }
        }

        return count;
    }

    @Test
    List<Double> queryTextTest(String query) {
        List<Double> queryVector = new ArrayList<>();
        StringBuilder strQueryVector = new StringBuilder();

        //调用 python 函数得到查询文本的特征向量
        try {
            //执行 py 文件
            String[] args1 = new String[]{"E:\\Git\\lavis2\\venv\\Scripts\\python.exe", "E:\\Git\\lavis2\\textExtractor.py", query};
            Process proc = Runtime.getRuntime().exec(args1);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            //读取特征向量文件

            String line = null;
            while ((line = in.readLine()) != null) {
                strQueryVector.append(line);
            }
            in.close();

            //将特征向量转化为浮点数组
            queryVector = VectorUtil.avsQueryStrToDouble(String.valueOf(strQueryVector));

            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return queryVector;
    }

    public void savePathToUrlList(List<String> urlList, Map<String, Double> sortMap) {
        for (String path : sortMap.keySet()) {
            urlList.add(path);
        }
    }


    public List<Double> getNewQueryVector(List<Double> vectorListDouble, Double vectorCosineSimilarity) {
        List<Double> newQueryVector = new ArrayList<>();
        StringBuilder strQueryVector = new StringBuilder();

        String vectorList = vectorListDouble.toString();
        vectorList = vectorList.substring(1, vectorList.length() - 1);

        //调用 python 函数得到新的查询向量
        try {
            //执行 py 文件
            String[] args1 = new String[]{"E:\\Git\\lavis2\\venv\\Scripts\\python.exe", "E:\\Git\\lavis2\\qir.py", String.valueOf(vectorCosineSimilarity), vectorList};
            Process proc = Runtime.getRuntime().exec(args1);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            //读取特征向量文件
            String line = null;
            while ((line = in.readLine()) != null) {
                strQueryVector.append(line);
            }
            in.close();

            //将特征向量转化为浮点数组
            newQueryVector = VectorUtil.strToDouble(String.valueOf(strQueryVector), 2);

            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return newQueryVector;
    }

    @Test
    void feedbackTest() {
        int query = 1680;
        int[] topKs = {5, 10, 100, 200};

        String[] querys = new String[]{"shot00396_75", "shot00644_34", "shot01228_29", "shot05410_94", "shot06131_53"};

        for (int topK : topKs) {
            Map<String, Double> scoreMap = new HashMap<>();


            for (int i = 0; i < querys.length; i++) {

                String queryNumber = Integer.toString(query).substring(1);
                CsvReader reader = CsvUtil.getReader();
                String csvPath = "D:\\Download\\VBSDataset\\grand_truth\\" + queryNumber + ".csv";
                List<GrandTruthResult> result = reader.read(ResourceUtil.getUtf8Reader(csvPath), GrandTruthResult.class);

                Query queryVector = queryMapper.selectById(query - 1000);
                List<Double> queryVectorList = VectorUtil.strToDouble(queryVector.getVector(), 1);

                String shot = querys[i];
                String feedBackVector = "";

                for (GrandTruthResult grandTruthResult : result) {
                    //特征向量，并转成浮点数组
                    List<Double> vectorDoubleList = VectorUtil.strToDouble(grandTruthResult.getVector(), 1);

                    //计算查询文本和图片的相似度得分
                    Double cosineSimilarity = VectorUtil.getCosineSimilarity(queryVectorList, vectorDoubleList);

                    scoreMap.put(PathUtils.handleToGTPath(grandTruthResult.getShot()), cosineSimilarity);

                    if (Objects.equals(PathUtils.handleToGTPath(grandTruthResult.getShot()), shot)) {
                        feedBackVector = grandTruthResult.getVector();
                    }
                }

                double cos = scoreMap.get(shot);

                List<Double> newQueryVector = getNewQueryVector(VectorUtil.strToDouble(feedBackVector, 1), cos);

                for (GrandTruthResult grandTruthResult : result) {
                    //特征向量，并转成浮点数组
                    List<Double> vectorDoubleList = VectorUtil.strToDouble(grandTruthResult.getVector(), 1);

                    //计算查询文本和图片的相似度得分
                    Double cosineSimilarity = VectorUtil.getCosineSimilarity(newQueryVector, vectorDoubleList);

                    String mapKey = PathUtils.handleToGTPath(grandTruthResult.getShot());
                    Double oldValue = scoreMap.get(mapKey);
                    scoreMap.replace(mapKey, oldValue + cosineSimilarity);
                }

                VectorUtil.mapNormalization(scoreMap);
                Map<String, Double> rankMap = VectorUtil.sortMapByValues(scoreMap);


                //查询结果的路径
                List<String> urlList = new ArrayList<>();

                for (String key : rankMap.keySet()) {
                    urlList.add(key);
                }


                List<String> topList = urlList.subList(0, topK);

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

                System.out.println("feed back " + (i + 1));
                System.out.println("top K = " + topK);
                System.out.println("predict true count = " + count);
                System.out.println("precision@" + topK + " = " + ((double) count / topK));
                System.out.println();
            }
        }
    }

    @Test
    void labelTest() {
        int topK = 200;
        int total = 0;

        int query;

        for (query = 1661; query <= 1680; query++) {

            Map<String, Double> scoreMap = new HashMap<>();//（路径，得分）键值对
            List<String> urlList = new ArrayList<>();//查询结果的路径
            Map<String, List<Double>> pathMap = new HashMap<>();//（路径，向量）键值对

            String queryNumber = Integer.toString(query).substring(1);
            CsvReader reader = CsvUtil.getReader();
            String csvPath = "D:\\Download\\VBSDataset\\grand_truth\\" + queryNumber + ".csv";
            List<GrandTruthResult> result = reader.read(ResourceUtil.getUtf8Reader(csvPath), GrandTruthResult.class);

            List<String> pathList = new ArrayList<>();

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

            String str = getLabelsByQuery(queryVector.getQuery());
            List<String> labels = new ArrayList<>(Arrays.asList(str.substring(1, str.length() - 1).split("'")));
            labels.removeIf(s -> s.length() < 2);
            System.out.println(labels);

            for (String label : labels) {
                List<Double> labelVectorList = getTextVector(label);

                for (String key : scoreMap.keySet()) {
                    List<Double> vectorDoubleList = pathMap.get(key);

                    //计算查询文本和图片的相似度得分
                    Double cosineSimilarity = VectorUtil.getCosineSimilarity(labelVectorList, vectorDoubleList);


                    Double oldValue = scoreMap.get(key);

                    //更新得分
                    scoreMap.replace(key, oldValue + cosineSimilarity * 0.02);
                }
            }

            //将（路径，得分）的键值对按得分降序
            Map<String, Double> sortMap = VectorUtil.sortMapByValues(scoreMap);


            //将路径存入urlList
            savePathToUrlList(urlList, sortMap);
//            System.out.println(urlList);


            List<String> topList = urlList.subList(0, topK);

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
                    }
                }
            }
            total += count;

            System.out.println("top K = " + topK);
            System.out.println("predict true count = " + count);
            System.out.println("precision@" + topK + " = " + ((double) count / topK));
            System.out.println();
        }

        System.out.println("mean precision@" + topK + " = " + ((double) total) / topK / 20);
    }


    @Test
    String getLabelsByQuery(String query) {
        StringBuilder strLabels = new StringBuilder();
        //调用 python 函数
        try {
            //执行 py 文件
            String[] args1 = new String[]{"E:\\Git\\lavis2\\venv\\Scripts\\python.exe", "E:\\Git\\lavis2\\keyphraseExtract.py", query};
            Process proc = Runtime.getRuntime().exec(args1);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            //读取输出
            String line = null;
            while ((line = in.readLine()) != null) {
                strLabels.append(line);
            }
            in.close();

            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return strLabels.toString();
    }

    public List<Double> getTextVector(String query) {
        List<Double> queryVector;

        //调用 python 函数得到查询文本的特征向量
        String[] args1 = new String[]{"E:\\Git\\lavis2\\venv\\Scripts\\python.exe", "E:\\Git\\lavis2\\textExtractor.py", query};
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

            //读取输出
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

    void qmr(String shot) {
        double alpha_dt = Math.sqrt(scoreMap.get(shot));
        double beta_dt = Math.sqrt(1 - scoreMap.get(shot));

        for (String path : pathList) {
            double probability = scoreMap.get(path);

            double cosineSimilarity = VectorUtil.getCosineSimilarity(vectorMap.get(path), vectorMap.get(shot));

            double alpha_d = Math.sqrt(probability);
            double beta_d = Math.sqrt(1 - probability);
            double my_lambda = alpha_d * alpha_dt + beta_d * beta_dt;
            double my_probability = Math.pow(my_lambda * alpha_dt, 2);
            scoreMap.replace(path, my_probability * cosineSimilarity);
        }
    }

    void Rocchio(String shot, int bool){

        List<Double> shotVector = vectorMap.get(shot);

        for (String path : pathList) {
            List<Double> pathVector = vectorMap.get(path);

            double cosineSimilarity = VectorUtil.getCosineSimilarity(shotVector, pathVector);
            if (bool == 0){
                scoreMap.replace(path, scoreMap.get(path) + 0.5 * cosineSimilarity);
            } else if (bool == 1) {
                scoreMap.replace(path, scoreMap.get(path) - 0.1 * cosineSimilarity);
            }
        }
    }


}
