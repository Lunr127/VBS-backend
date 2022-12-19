package whu.vbs;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import org.apache.ibatis.jdbc.ScriptRunner;
import whu.vbs.Entity.CsvFile.CsvTest;
import whu.vbs.Entity.CsvFile.GrandTruth;
import whu.vbs.Mapper.GrandTruthMapper;
import whu.vbs.Mapper.VectorMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import whu.vbs.Entity.VectorResult;
import whu.vbs.utils.VectorUtil;


import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@SpringBootTest
class VbsApplicationTests {

    @Autowired
    private VectorMapper vectorMapper;

    @Autowired
    private GrandTruthMapper grandTruthMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void vectorTest() {
        List<String> urlList = new ArrayList<>();
        Map<String, List<Double>> vectorMap = new HashMap<>();
        Map<String, Double> scoreMap = new HashMap<>();

        System.out.println("--------vector test---------");
        List<VectorResult> vectorResultList = vectorMapper.selectList(null);

        //查询向量
        List<Double> query = VectorUtil.strToDouble(vectorResultList.get(0).getVector(), 1);

        for (VectorResult vectorResult : vectorResultList) {
            List<Double> f = VectorUtil.strToDouble(vectorResult.getVector(), 1);
            vectorMap.put(vectorResult.getPath(), f);
            Double cosineSimilarity = VectorUtil.getCosineSimilarity(query, f);
            scoreMap.put(vectorResult.getPath(), cosineSimilarity);
        }

        Map<String, Double> sortMap = VectorUtil.sortMapByValues(scoreMap);
        System.out.println("--------------this is result----------");
        for (String path : sortMap.keySet()) {
            path = path.substring(1);
            path = "E:\\Git\\towhee-main\\V3Ctest" + path;
            urlList.add(path);
        }

        for (String s : urlList) {
            System.out.println(s);
        }

    }

    @Test
    void pytest() {
        //query 为查询文本
        String query = "people riding bike on the street";

        List<Double> queryVector = new ArrayList<>();
        StringBuilder strQueryVector = new StringBuilder();

        //调用 python 函数得到查询文本的特征向量
        try {
            //执行 py 文件
            String[] args1 = new String[] { "E:\\Git\\towhee-main\\venv\\Scripts\\python.exe", "E:\\Git\\towhee-main\\getTextVector.py", query };
            Process proc = Runtime.getRuntime().exec(args1);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            //读取特征向量文件
            String line = null;
            while ((line = in.readLine()) != null) {
                strQueryVector.append(line);
            }
            in.close();

            //将特征向量转化为浮点数组
            //queryVector = VectorUtil.strToDouble(String.valueOf(strQueryVector),1);

            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(strQueryVector);
    }

    @Test
    void feedbackTest(){

        int id = 1;
        List<Double> newQueryVector = new ArrayList<>();
        StringBuilder strQueryVector = new StringBuilder();

        //根据 id 从数据库中得到反馈图片
        VectorResult positiveFeedBackVectorResult = vectorMapper.selectById(id);

        //得到反馈图片的特征向量
        List<Double> vectorListDouble = VectorUtil.strToDouble(positiveFeedBackVectorResult.getVector(), 1);
        String vectorList = vectorListDouble.toString();
        vectorList = vectorList.substring(1, vectorList.length() - 1);

        //反馈图片的概率得分 vectorCosineSimilarity
        double vectorCosineSimilarity = 0.8;

        //调用 python 函数得到新的查询向量
        try {
            //执行 py 文件
            String[] args1 = new String[] { "E:\\Git\\lavis2\\venv\\Scripts\\python.exe", "E:\\Git\\lavis2\\qir.py", String.valueOf(vectorCosineSimilarity), vectorList};
            Process proc = Runtime.getRuntime().exec(args1);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            //读取特征向量文件
            String line = null;
            while ((line = in.readLine()) != null) {
                strQueryVector.append(line);
            }
            in.close();

            //将特征向量转化为浮点数组
            newQueryVector = VectorUtil.strToDouble(String.valueOf(strQueryVector),2);

            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(newQueryVector);
    }


    @Test
    void strTest() {
        String s = "";
        s.replace("\n", "").replace("\r", "");
        System.out.println(s);
    }

    @Test
    void queryTest() {
        VectorResult vectorResult = vectorMapper.selectById(1);
        List<Double> vectorListDouble = VectorUtil.strToDouble(vectorResult.getVector(), 1);
        String vectorListStr = vectorListDouble.toString();
        String vectorList = vectorListStr.substring(1, vectorListStr.length() - 1);
        System.out.println(vectorList);


        StringBuilder strQueryVector = new StringBuilder();
        List<Double> queryVector = new ArrayList<>();

        //将反馈图片的特征向量 vectorList 写入文件 checkVector.txt
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("E:\\Git\\lavis2\\TextVector\\checkVector.txt"));
            out.write(vectorList.toString());
            out.close();
        } catch (IOException ignored) {
        }

        //调用 python 函数得到查询文本的特征向量
        try {
            //执行 py 文件
            Process proc = Runtime.getRuntime().exec("E:\\Git\\lavis2\\venv\\Scripts\\python.exe E:\\Git\\lavis2\\qir.py");

            //读取特征向量文件
            BufferedReader br = new BufferedReader(new FileReader("E:\\Git\\lavis2\\TextVector\\newQueryVector.txt"));
            String st;
            while ((st = br.readLine()) != null) {
                strQueryVector.append(st);
            }

            //将特征向量转化为浮点数组
            queryVector = VectorUtil.strToDouble(String.valueOf(strQueryVector), 2);

            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(queryVector.size());
        System.out.println(queryVector);
    }


    @Test
    void unbearable() {

        List<VectorResult> vectorResultList = vectorMapper.selectList(null);
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

        System.out.println(feedBackVector);

        int index = 0;
        for (VectorResult vectorResult : vectorResultList) {

            if (index > 5) {
                break;
            }

            //取出在库图片的特征向量，并转成浮点数组
            List<Double> vectorDoubleList = VectorUtil.strToDouble(vectorResult.getVector(), 1);

            //计算查询文本和图片的相似度得分
            double cosineSimilarity = VectorUtil.getCosineSimilarity(feedBackVector, vectorDoubleList);

            System.out.println("------------this is feedback vector--------------");
            System.out.println(feedBackVector);
            System.out.println("------------this is keyframe vector--------------");
            System.out.println(feedBackVector);
            System.out.println("id:" + vectorResult.getId() + "path:" + vectorResult.getPath() + "cos:" + cosineSimilarity);
            System.out.println();

            index++;
        }
    }

    @Test
    void test1() {
        //调用 python 函数得到查询文本的特征向量
        try {
            //执行 py 文件
            Process proc = Runtime.getRuntime().exec("python E:\\Git\\towhee-main\\qir.py");
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("exec successfully");

    }

    @Test
    void test2() {
        List<GrandTruth> grandTruths = grandTruthMapper.selectList(null);
        for (GrandTruth grandTruth : grandTruths) {
            if (grandTruth.getJudgment() != 1) {
                grandTruthMapper.deleteById(grandTruth.getId());
            }
        }
    }


    @Test
    void grandTruthTest() {
        int query = 1661;

        List<String> pathList = new ArrayList<>();
        pathList.add("shot00022_15");
        pathList.add("shot00015_17");
        pathList.add("shot00353_107");
        pathList.add("shot00057_190");
        pathList.add("shot00410_113");
        pathList.add("shot01238_106");

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
        System.out.println("query " + query + " number = " + number);

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

        System.out.println("count = " + count);
    }

    @Test
    void csv() {

        try {
            // jdbc 连接信息: 注: 现在版本的JDBC不需要配置driver，因为不需要Class.forName手动加载驱动
            // 建立连接
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/vbs?serverTimezone=UTC&useSSL=false", "root", "root");
            Statement statement = conn.createStatement();

            String fileName = "";
            for (int i = 3; i <= 20; i++) {

                if (i < 10) {
                    fileName = "0000" + i;
                } else if (i < 100) {
                    fileName = "000" + i;
                } else if (i < 1000) {
                    fileName = "00" + i;
                } else if (i < 10000) {
                    fileName = "0" + i;
                }

                CsvReader reader = CsvUtil.getReader();
                //从文件中读取CSV数据
                List<CsvTest> result = reader.read(ResourceUtil.getUtf8Reader("D:\\Download\\VBSDataset\\datacsv\\"+fileName+".csv"), CsvTest.class);
                StringBuilder s = new StringBuilder("INSERT INTO `vbs`.`vector_result` (`path`, `vector`) VALUES");

                for (int j = 2; j < result.size(); j++){
                    CsvTest csvTest = result.get(j);
                    int index1 = csvTest.getVector().indexOf('[');
                    int index2 = csvTest.getVector().indexOf(']');
                    csvTest.setId(csvTest.getId().substring(1));
                    csvTest.setVector(csvTest.getVector().substring(index1+1, index2+1));
                    s.append("('").append(csvTest.getId()).append("','").append(csvTest.getVector()).append("'),");
                }
                int index = s.lastIndexOf(",");
                String sql = s.substring(0, index);
                try {
                    statement.execute(sql);
                    // 若成功，打印提示信息
                    System.out.println("----------" + fileName + " success------------");
                } catch (SQLException e) {
                    System.out.println("----------" + fileName + " not success------------");
                }
            }
            // 关闭连接
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


//    @Test
//    void loadSql(String fileName, Statement statement, List<CsvTest> result){
//        String s = "INSERT INTO `vbs`.`vector_result` (`path`, `vector`) VALUES";
//        for (CsvTest csvTest: result){
//            s = s + "(" + csvTest.getId()+ "," + csvTest.getVector() + "),";
//        }
//
//
//        try {
//            statement.execute(sql);
//            // 若成功，打印提示信息
//            System.out.println("----------" + fileName + " success------------");
//        } catch (SQLException e) {
//            System.out.println("----------" + fileName + " not success------------");
//        }
//    }

    @Test
    //将 csv 文件导入数据库中
    void csvTest() {
        try {
            // jdbc 连接信息: 注: 现在版本的JDBC不需要配置driver，因为不需要Class.forName手动加载驱动
            // 建立连接
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/vbs?serverTimezone=UTC&useSSL=false", "root", "root");
            Statement statement = conn.createStatement();
            // 创建ScriptRunner，用于执行SQL脚本
            ScriptRunner runner = new ScriptRunner(conn);
            runner.setErrorLogWriter(null);
            runner.setLogWriter(null);

            String fileName = "";
            for (int i = 1; i <= 1; i++) {
                if (i < 10) {
                    fileName = "0000" + i;
                } else if (i < 100) {
                    fileName = "000" + i;
                } else if (i < 1000) {
                    fileName = "00" + i;
                } else if (i < 10000) {
                    fileName = "0" + i;
                }
                loadFile(fileName, statement);
            }
            // 关闭连接
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    void loadFile(String fileName, Statement statement) {
        String sql = "load data LOCAL infile 'D:/Download/VBSDataset/test/"+fileName+".csv'\n" +
                "into table vector_result\n" +
                "fields terminated by ','\n" +
                "enclosed by '\"'\n" +
                "lines terminated by '\\r\\n'\n" +
                "ignore 1 lines(path, vector);";
        try {
            statement.execute(sql);
            // 若成功，打印提示信息
            System.out.println("----------" + fileName + " success------------");
        } catch (SQLException e) {
            System.out.println("----------" + fileName + " not success------------");
        }
    }

    @Test
    void test4(){
        VectorResult vectorResult = vectorMapper.selectById(2650);
        int index = vectorResult.getVector().indexOf(']');
        vectorResult.setVector(vectorResult.getVector().substring(0, index+1));
        System.out.println(vectorResult.getVector());
    }

}
