package whu.vbs.Service;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jblas.DoubleMatrix;
import org.jblas.Singular;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import whu.vbs.DRES.dev.dres.ApiClient;
import whu.vbs.DRES.dev.dres.ApiException;
import whu.vbs.DRES.dev.dres.Settings;
import whu.vbs.DRES.dev.dres.client.ClientRunInfoApi;
import whu.vbs.DRES.dev.dres.client.LogApi;
import whu.vbs.DRES.dev.dres.client.SubmissionApi;
import whu.vbs.DRES.dev.dres.client.UserApi;
import whu.vbs.DRES.dev.dres.org.openapitools.client.model.*;
import whu.vbs.Entity.CsvFile.AvsQuery;
import whu.vbs.Entity.CsvFile.GrandTruthResult;
import whu.vbs.Entity.CsvFile.ImageFilter;
import whu.vbs.Entity.CsvFile.MasterShotBoundary;
import whu.vbs.Mapper.*;
import whu.vbs.utils.PathUtils;
import whu.vbs.utils.VectorUtil;

import java.io.*;
import java.net.ConnectException;
import java.nio.file.Files;
import java.util.*;

@Service
public class AvsService {

    @Autowired
    AvsGrandTruthMapper avsGrandTruthMapper;

    @Autowired
    GrandTruthMapper grandTruthMapper;

    @Autowired
    MasterShotBoundaryMapper masterShotBoundaryMapper;

    @Autowired
    AvsQueryMapper avsQueryMapper;

    @Autowired
    VectorMapper vectorMapper;

    @Autowired
    ImageFilterMapper imageFilterMapper;

    Map<String, Double> scoreMap = new HashMap<>();//（路径，得分）键值对
    Map<String, List<Double>> pathMap = new HashMap<>();//（路径，向量）键值对
    List<String> pathList = new ArrayList<>();

    Map<String, Double> msbMap = new HashMap<>();

    List<Double> queryVector = new ArrayList<>();

    int topK = 1000;
    int showTopK = 500;
    int query = 1661;

    double pos = 0.01;
    double pos_bar = 0.001;
    double neg = 0.003;
    double neg_bar = 0.0001;

    int submitIndex = 0;

    int topKIndex = 0;


    Map<Integer, String> queryMap = new HashMap<>();

    List<String> urlList = new ArrayList<>();

    Map<String, Integer> videoInUrlListMap = new HashMap<>();


    public List<Map<String, String>> getInitTopK() {
        int showTopK = 500;
        submitIndex = 0;
        topKIndex = 0;
        scoreMap = new HashMap<>();

        List<ImageFilter> imageFilters = imageFilterMapper.selectList(null);
        List<String> imageFilterList = new ArrayList<>();
        for (ImageFilter imageFilter : imageFilters) {
            imageFilterList.add(imageFilter.getShot());
        }

        CsvReader reader = CsvUtil.getReader();
        String csvPath = "D:\\Download\\VBSDataset\\VBS_task\\Init_top10000\\init.csv";
        List<GrandTruthResult> resultList = reader.read(ResourceUtil.getUtf8Reader(csvPath), GrandTruthResult.class);

        cn.hutool.core.io.file.FileReader fileReader = new FileReader("D:\\Download\\VBSDataset\\VBS_task\\Init_top10000\\query.txt");
        String queryFile = fileReader.readString();
        queryVector = VectorUtil.strToDouble(queryFile, 2);

        for (GrandTruthResult grandTruthResult : resultList) {
            String path = PathUtils.handleToGTPath(grandTruthResult.getShot());
            if (imageFilterList.contains(path)) {
                continue;
            }
            pathList.add(PathUtils.handleToGTPath(grandTruthResult.getShot()));

            //特征向量，并转成浮点数组
            List<Double> vectorDoubleList = VectorUtil.strToDouble(grandTruthResult.getVector(), 1);

            //计算查询和图片的相似度得分
            Double cosineSimilarity = VectorUtil.getCosineSimilarity(queryVector, vectorDoubleList);

            //建立（路径，得分）的键值对
            scoreMap.put(PathUtils.handleToGTPath(grandTruthResult.getShot()), cosineSimilarity);

            pathMap.put(PathUtils.handleToGTPath(grandTruthResult.getShot()), vectorDoubleList);
        }

//        for (String shot : pathList) {
//            String videoId = shot.substring(4, 9);
//            String shotId = shot.substring(shot.indexOf("_") + 1);
//
//            QueryWrapper<MasterShotBoundary> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq("video_id", videoId);
//            List<MasterShotBoundary> boundaryList = masterShotBoundaryMapper.selectList(queryWrapper);
//            MasterShotBoundary boundary = boundaryList.get(Integer.parseInt(shotId) - 1);
//
//            Double startTime = Double.parseDouble(boundary.getStartTime()) * 1000;
//            Double endTime = Double.parseDouble(boundary.getEndTime()) * 1000;
//            double frameTime = (startTime + endTime) / 2;
//            msbMap.put(shot, frameTime);
//        }


//        //得分归一化
//        VectorUtil.mapNormalization(scoreMap);

        //将（路径，得分）的键值对按得分降序
        Map<String, Double> sortMap = VectorUtil.sortMapByValues(scoreMap);

        List<String> urlInitList = new ArrayList<>();//查询结果的路径
        //将路径存入urlList
        PathUtils.savePathToUrlList(urlInitList, sortMap);

        videoInUrlListMap = new HashMap<>();
        urlList = new ArrayList<>();

        for (String shot : urlInitList) {
            String videoId = shot.substring(4, 9);
            if (videoInUrlListMap.get(videoId) != null) {
                videoInUrlListMap.replace(videoId, videoInUrlListMap.get(videoId) + 1);
            } else {
                videoInUrlListMap.put(videoId, 1);
            }
            if (videoInUrlListMap.get(videoId) > 7) {
                continue;
            }
            urlList.add(shot);
        }


//        submitLog();

        return PathUtils.urlToBase64List(urlList, showTopK);
    }


    public void setQueryMap() {
        queryMap.put(1, "showing one person playing a guitar (other people but no other musicians may be visible)");
        queryMap.put(2, "one or more persons balancing on a bar, railing, rope or slackline, without any device under their feet");
        queryMap.put(4, "someone riding a horse or sitting on a horse (living animal)");
        queryMap.put(5, "taken from any vehicle driving inside a tunnel, requiring part of the vehicle being visible");
        queryMap.put(6, "outdoor shots showing a teddy bear (toy)");
        queryMap.put(7, "a waterfall, without people");
        queryMap.put(9, "one or more decorated trees (not just branches) that are not lit (inside or outside)");
        queryMap.put(10, "someone with their hands on a camera (not e.g. a phone-like device), filming or taking/preparing to take a picture");
    }

//    public List<Map<String, String>> topKTest(String queryStr) {
//
//        scoreMap = new HashMap<>();
//        pathMap = new HashMap<>();
//
//        setQueryMap();
//        for (Integer key : queryMap.keySet()) {
//            if (queryStr.equals(queryMap.get(key))) {
//                query = key;
//            }
//        }
//
//
//        CsvReader reader = CsvUtil.getReader();
//        String csvPath = "D:\\Download\\VBSDataset\\blip_v3c1_top10000\\" + query + ".csv";
//        List<GrandTruthResult> result = reader.read(ResourceUtil.getUtf8Reader(csvPath), GrandTruthResult.class);
//
//
//        AvsQuery queryVector = avsQueryMapper.selectById(query);
//
//        List<Double> queryVectorList = VectorUtil.strToDouble(queryVector.getVector(), 1);
//
//
//        for (GrandTruthResult grandTruthResult : result) {
//            pathList.add(PathUtils.handleToGTPath(grandTruthResult.getShot()));
//
//            //特征向量，并转成浮点数组
//            List<Double> vectorDoubleList = VectorUtil.strToDouble(grandTruthResult.getVector(), 1);
//
//            //计算查询文本和图片的相似度得分
//            Double cosineSimilarity = VectorUtil.getCosineSimilarity(queryVectorList, vectorDoubleList);
//
//            //建立（路径，得分）的键值对
//            scoreMap.put(PathUtils.handleToGTPath(grandTruthResult.getShot()), cosineSimilarity);
//
//            //建立（路径，向量）的键值对
//            pathMap.put(PathUtils.handleToGTPath(grandTruthResult.getShot()), vectorDoubleList);
//        }
//
//        //initial
//        VectorUtil.mapNormalization(scoreMap);
//
//        //将（路径，得分）的键值对按得分降序
//        Map<String, Double> sortMap = VectorUtil.sortMapByValues(scoreMap);
//
//        List<String> urlList = new ArrayList<>();//查询结果的路径
//        //将路径存入urlList
//        savePathToUrlList(urlList, sortMap);
//        List<String> topList = urlList.subList(0, topK);
//
//
//        List<Map<String, String>> base64List = new ArrayList<>();
//        for (String shot : topList.subList(0, showTopK)) {
//            Map<String, String> base64Map = new HashMap<>();// (base64，路径)键值对
//            String base64 = "data:image/png;base64," + imgToBase64(shot);
//            base64Map.put("shot", shot);
//            base64Map.put("base64", base64);
//            base64List.add(base64Map);
//        }
//        return base64List;
//
//    }


    public List<Map<String, String>> reRank(List<String> LikePaths, List<String> NotLikePaths) {

        int showTopK = 500;

        qir_v4(LikePaths, NotLikePaths, pos, pos_bar, neg, neg_bar);

//        //得分归一化
//        VectorUtil.mapNormalization(scoreMap);

        for (String shot : LikePaths) {
            if (scoreMap.get(shot) == null) {
                continue;
            }
            scoreMap.replace(shot, 1000.0 - topKIndex);
            topKIndex++;
        }
        int j = 0;
        for (String shot : NotLikePaths) {
            if (scoreMap.get(shot) == null) {
                continue;
            }
            scoreMap.replace(shot, -1000.0 - j);
            j++;
        }

//        //得分归一化
//        VectorUtil.mapNormalization(scoreMap);

        //将（路径，得分）的键值对按得分降序
        Map<String, Double> sortMap = VectorUtil.sortMapByValues(scoreMap);

//        submitLog();

        List<String> urlInitList = new ArrayList<>();//查询结果的路径
        //将路径存入urlList
        PathUtils.savePathToUrlList(urlInitList, sortMap);

        videoInUrlListMap = new HashMap<>();
        urlList = new ArrayList<>();

        for (String shot : urlInitList) {
            String videoId = shot.substring(4, 9);
            if (videoInUrlListMap.get(videoId) != null) {
                videoInUrlListMap.replace(videoId, videoInUrlListMap.get(videoId) + 1);
            } else {
                videoInUrlListMap.put(videoId, 1);
            }
            if (videoInUrlListMap.get(videoId) > 10) {
                continue;
            }
            urlList.add(shot);
        }


        return PathUtils.urlToBase64List(urlList, showTopK);

    }


    public void qir_v4(List<String> posPaths, List<String> negPaths, double pos, double pos_bar, double neg, double neg_bar) {

        DoubleMatrix pos_vector = new DoubleMatrix(1, 256);
        DoubleMatrix complementary_pos_vector = new DoubleMatrix(1, 256);
        DoubleMatrix neg_vector = new DoubleMatrix(1, 256);
        DoubleMatrix complementary_neg_vector = new DoubleMatrix(1, 256);
        DoubleMatrix init_vector = new DoubleMatrix(1, 256);


        //若有正反馈，则计算正反馈向量的平均以及补空间向量的平均
        if (posPaths.size() > 0) {
            for (String path : posPaths) {
                if (path.length() < 5) {
                    break;
                }
                //得到选中的反馈图片的向量
                String selectedVector = pathMap.get(path).toString();
                selectedVector = selectedVector.substring(1, selectedVector.length() - 1);
                //进行格式转化
                List<Double> selectedVector_1 = VectorUtil.strToDouble(selectedVector, 0);
                Double[] selectedVector_2 = selectedVector_1.toArray(selectedVector_1.toArray(new Double[selectedVector_1.size()]));
                double[] selectedVector_3 = new double[selectedVector_2.length];
                for (int i = 0; i < selectedVector_2.length; i++) selectedVector_3[i] = selectedVector_2[i];
                double[][] selectedVector_4 = {selectedVector_3};
                //格式转换完成，得到DoubleMatrix格式的向量
                DoubleMatrix selectedVector_5 = new DoubleMatrix(selectedVector_4);

                //累加初始正反馈
                pos_vector = pos_vector.add(selectedVector_5);

                //计算补空间向量
                DoubleMatrix V = Singular.fullSVD(selectedVector_5)[2];
                DoubleMatrix nullspace = new DoubleMatrix(V.rows, 1);
                //将补空间基向量累加
                for (int i = 1; i < V.columns; i++)
                    nullspace = nullspace.add(V.getColumn(i));
                complementary_pos_vector = nullspace.mul(1.0 / (V.columns) - 1);

            }

            //归一化作平均
            pos_vector = pos_vector.mul(1.0 / posPaths.size());
            complementary_pos_vector = complementary_pos_vector.mul(1.0 / posPaths.size());
        }

        //若有负反馈，则计算负反馈向量的平均以及补空间向量的平均
        if (negPaths.size() > 0) {
            for (String path : negPaths) {
                if (path.length() < 5) {
                    break;
                }
                //得到选中的反馈图片的向量
                String selectedVector = pathMap.get(path).toString();
                selectedVector = selectedVector.substring(1, selectedVector.length() - 1);
                //进行格式转化
                List<Double> selectedVector_1 = VectorUtil.strToDouble(selectedVector, 0);
                Double[] selectedVector_2 = selectedVector_1.toArray(selectedVector_1.toArray(new Double[selectedVector_1.size()]));
                double[] selectedVector_3 = new double[selectedVector_2.length];
                for (int i = 0; i < selectedVector_2.length; i++) selectedVector_3[i] = selectedVector_2[i];
                double[][] selectedVector_4 = {selectedVector_3};
                //格式转换完成，得到DoubleMatrix格式的向量
                DoubleMatrix selectedVector_5 = new DoubleMatrix(selectedVector_4);

                //累加初始正反馈
                neg_vector = neg_vector.add(selectedVector_5);

                //计算补空间向量
                DoubleMatrix V = Singular.fullSVD(selectedVector_5)[2];
                DoubleMatrix nullspace = new DoubleMatrix(V.rows, 1);
                //将补空间基向量累加
                for (int i = 1; i < V.columns; i++)
                    nullspace = nullspace.add(V.getColumn(i));
                complementary_neg_vector = nullspace.mul(1.0 / (V.columns) - 1);

            }

            //归一化作平均
            neg_vector = neg_vector.mul(1.0 / negPaths.size());
            complementary_neg_vector = complementary_neg_vector.mul(1.0 / negPaths.size());
        }


        double[] newQueryVector_pos = pos_vector.data;
        double[] newQueryVector_pos_bar = complementary_pos_vector.data;
        double[] newQueryVector_neg = neg_vector.data;
        double[] newQueryVector_neg_bar = complementary_neg_vector.data;


        String str_pos = Arrays.toString(newQueryVector_pos);
        String str_pos_bar = Arrays.toString(newQueryVector_pos_bar);
        String str_neg = Arrays.toString(newQueryVector_neg);
        String str_neg_bar = Arrays.toString(newQueryVector_neg_bar);

        List<Double> vec_pos = VectorUtil.strToDouble(String.valueOf(str_pos), 2);
        List<Double> vec_pos_bar = VectorUtil.strToDouble(String.valueOf(str_pos_bar), 2);
        List<Double> vec_neg = VectorUtil.strToDouble(String.valueOf(str_neg), 2);
        List<Double> vec_neg_bar = VectorUtil.strToDouble(String.valueOf(str_neg_bar), 2);

        for (String path : pathList) {
            List<Double> pathVector = pathMap.get(path);
            double cosineSimilarity_1 = VectorUtil.getCosineSimilarity(vec_pos, pathVector);
            double cosineSimilarity_2 = VectorUtil.getCosineSimilarity(vec_pos_bar, pathVector);
            double cosineSimilarity_3 = VectorUtil.getCosineSimilarity(vec_neg, pathVector);
            double cosineSimilarity_4 = VectorUtil.getCosineSimilarity(vec_neg_bar, pathVector);

            if (scoreMap.get(path) != null) {
                scoreMap.replace(path, scoreMap.get(path) + pos * cosineSimilarity_1 - pos_bar * cosineSimilarity_2 + neg * cosineSimilarity_3 + neg_bar * cosineSimilarity_4);
            } else {
                scoreMap.put(path, pos * cosineSimilarity_1 - pos_bar * cosineSimilarity_2 + neg * cosineSimilarity_3 + neg_bar * cosineSimilarity_4);
            }

        }

    }


    public String submit(int submitInput) {
        ApiClient client = new ApiClient().setBasePath(Settings.BASE_PATH);

        //initialize user api client
        UserApi userApi = new UserApi(client);

        //initialize evaluation run info client
        ClientRunInfoApi runInfoApi = new ClientRunInfoApi(client);

        //initialize submission api client
        SubmissionApi submissionApi = new SubmissionApi(client);

        //initialize logging api client
        LogApi logApi = new LogApi(client);

        System.out.println("Trying to log in to '" + Settings.BASE_PATH + "' with user '" + Settings.USER + "'");

        //login request
        UserDetails login = null;
        try {
            login = userApi.postApiV1Login(new LoginRequest().username(Settings.USER).password(Settings.PASS));
        } catch (ApiException e) {

            if (e.getCause() instanceof ConnectException) {
                System.err.println("Could not connect to " + Settings.BASE_PATH + ", exiting");
            } else {
                System.err.println("Error during login request: '" + e.getMessage() + "', exiting");
            }
            return "submit error";
        }

        System.out.println("login successful");
        System.out.println("user: " + login.getUsername());
        System.out.println("role: " + login.getRole().getValue());
        System.out.println("session: " + login.getSessionId());

        //store session token for future requests
        String sessionId = login.getSessionId();

        ClientRunInfoList currentRuns = null;
        try {
            currentRuns = runInfoApi.getApiV1ClientRunInfoList(sessionId);
        } catch (ApiException e) {
            System.err.println("Error during request: '" + e.getMessage() + "', exiting");
            return "submit error";
        }

        System.out.println("Found " + currentRuns.getRuns().size() + " ongoing evaluation runs");

        for (ClientRunInfo run : currentRuns.getRuns()) {
            System.out.println(run.getName() + " (" + run.getId() + "): " + run.getStatus());
            if (run.getDescription() != null) {
                System.out.println(run.getDescription());
            }
            System.out.println();
        }

        SuccessfulSubmissionsStatus submitStatus = null;

        for (int i = 0; i < submitInput; i++) {
            if (i >= urlList.size() - 1) {
                break;
            }
            String shot = urlList.get(submitIndex);
            submitIndex++;
            String videoId = shot.substring(4, 9);
            String shotId = shot.substring(shot.indexOf("_") + 1);

            QueryWrapper<MasterShotBoundary> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("video_id", videoId);
            List<MasterShotBoundary> boundaryList = masterShotBoundaryMapper.selectList(queryWrapper);
            MasterShotBoundary boundary = boundaryList.get(Integer.parseInt(shotId) - 1);

            Double startTime = Double.parseDouble(boundary.getStartTime()) * 1000;
            Double endTime = Double.parseDouble(boundary.getEndTime()) * 1000;
            double frameTime = (startTime + endTime) / 2;

            int second = (int) (frameTime / 1000);
            int HH = second / 3600;
            int MM = second / 60 - 60 * HH;
            int SS = second - 60 * MM;

            String strHH = HH < 10 ? "0" + HH : String.valueOf(HH);
            String strMM = MM < 10 ? "0" + MM : String.valueOf(MM);
            String strSS = SS < 10 ? "0" + SS : String.valueOf(SS);
            String strFF = "00";
            String submitTime = strHH + ":" + strMM + ":" + strSS + ":" + strFF;

            SuccessfulSubmissionsStatus submissionResponse = null;
            try {
                submissionResponse = submissionApi.getApiV1Submit(
                        null, //does not usually need to be set
                        videoId, //item which is to be submitted
                        null, //in case the task is not targeting a particular content object but plaintext
                        null, // for items with temporal components, such as video
                        null,  // only one of the time fields needs to be set.
                        submitTime, //in this case, we use the timestamp in the form HH:MM:SS:FF
                        sessionId
                );
                if (submissionResponse != null && submissionResponse.getStatus()) {
                    submitStatus = submissionResponse;
                }
                System.out.println("submit" + videoId + " " + submitTime + " Successfully");
            } catch (ApiException e) {
                switch (e.getCode()) {
                    case 401: {
                        System.err.println("There was an authentication error during the submission. Check the session id.");
                        break;
                    }
                    case 404: {
                        System.err.println("There is currently no active task which would accept submissions.");
                        break;
                    }
                    default: {
                        System.err.println("Something unexpected went wrong during the submission: '" + e.getMessage() + "'.");
                    }
                }
            }

        }
        SuccessStatus logout = null;


        if (submitStatus != null && submitStatus.getStatus()) {
            System.out.println("The submission was successfully sent to the server.");


            List<QueryResult> queryResultList = new ArrayList<>();

            int rankIndex = 1;
            for (String url : urlList) {
                String itemName = url.substring(4, 9);
                Integer segment = Integer.valueOf(url.substring(10));
                QueryResult queryResult = new QueryResult().item(itemName).segment(segment).score(scoreMap.get(url)).rank(rankIndex);
                queryResultList.add(queryResult);
                rankIndex++;
            }

            try {
                logApi.postApiV1LogResult(
                        sessionId,
                        new QueryResultLog()
                                .timestamp(System.currentTimeMillis())
                                .sortType("list")
                                .results(queryResultList)
                                .events(Collections.emptyList())
                                .resultSetAvailability("")
                );
                System.out.println("The log submit successfully");
            } catch (ApiException e) {
                System.err.println("Error during request: '" + e.getMessage() + "'");
            }

        }


        try {
            logout = userApi.getApiV1Logout(sessionId);
        } catch (ApiException e) {
            System.err.println("Error during request: '" + e.getMessage() + "'");
        }

        if (logout != null && logout.getStatus()) {
            System.out.println("Successfully logged out");
        }

        return "submit success";
    }


    public void submitLog() {
        ApiClient client = new ApiClient().setBasePath(Settings.BASE_PATH);

        //initialize user api client
        UserApi userApi = new UserApi(client);

        //initialize evaluation run info client
        ClientRunInfoApi runInfoApi = new ClientRunInfoApi(client);

        //initialize submission api client
        SubmissionApi submissionApi = new SubmissionApi(client);

        //initialize logging api client
        LogApi logApi = new LogApi(client);

        System.out.println("Trying to log in to '" + Settings.BASE_PATH + "' with user '" + Settings.USER + "'");

        //login request
        UserDetails login = null;
        try {
            login = userApi.postApiV1Login(new LoginRequest().username(Settings.USER).password(Settings.PASS));
        } catch (ApiException e) {

            if (e.getCause() instanceof ConnectException) {
                System.err.println("Could not connect to " + Settings.BASE_PATH + ", exiting");
            } else {
                System.err.println("Error during login request: '" + e.getMessage() + "', exiting");
            }
            return;
        }

        //store session token for future requests
        String sessionId = login.getSessionId();

        ClientRunInfoList currentRuns = null;
        try {
            currentRuns = runInfoApi.getApiV1ClientRunInfoList(sessionId);
        } catch (ApiException e) {
            System.err.println("Error during request: '" + e.getMessage() + "', exiting");
            return;
        }

        System.out.println("Found " + currentRuns.getRuns().size() + " ongoing evaluation runs");

        for (ClientRunInfo run : currentRuns.getRuns()) {
            System.out.println(run.getName() + " (" + run.getId() + "): " + run.getStatus());
            if (run.getDescription() != null) {
                System.out.println(run.getDescription());
            }
            System.out.println();
        }


        List<QueryResult> queryResultList = new ArrayList<>();

        int rankIndex = 1;
        for (String shot : urlList) {
            String itemName = shot.substring(4, 9);
            Integer segment = Integer.valueOf(shot.substring(10));
            QueryResult queryResult = new QueryResult().item(itemName).segment(segment).score(scoreMap.get(shot)).rank(rankIndex);
            queryResultList.add(queryResult);
            rankIndex++;
        }

        try {
            logApi.postApiV1LogResult(
                    sessionId,
                    new QueryResultLog()
                            .timestamp(System.currentTimeMillis())
                            .sortType("list")
                            .results(queryResultList)
                            .events(Collections.emptyList())
                            .resultSetAvailability("")
            );
            System.out.println("The log submit successfully");
        } catch (ApiException e) {
            System.err.println("Error during request: '" + e.getMessage() + "'");
        }

        SuccessStatus logout = null;

        try {
            logout = userApi.getApiV1Logout(sessionId);
        } catch (ApiException e) {
            System.err.println("Error during request: '" + e.getMessage() + "'");
        }

        if (logout != null && logout.getStatus()) {
            System.out.println("Successfully logged out");
        }
    }


    public void feedBack(List<String> Paths, int bool) {

        if (Paths.get(0).length() < 5) {
            return;
        }

        //对每一个反馈图片
        for (String path : Paths) {

            //得到选中的反馈图片的向量
            String selectedVector = pathMap.get(path).toString();
            selectedVector = selectedVector.substring(1, selectedVector.length() - 1);

            //反馈图片的概率得分 vectorCosineSimilarity
            Double selectedCos = scoreMap.get(path);

            //调用 python 函数得到新的查询向量
            String[] args1 = new String[]{"E:\\Git\\lavis2\\venv\\Scripts\\python.exe", "E:\\Git\\lavis2\\qir.py", selectedVector, selectedCos.toString()};
            String strQueryVector = runPython(args1);
            List<Double> newQueryVector = VectorUtil.strToDouble(String.valueOf(strQueryVector), 2);

            //更新所有图片的概率得分
            reRankByNewQuery(newQueryVector, bool);
        }
    }


    public void reRankByNewQuery(List<Double> queryVector, int bool) {

        for (String path : pathMap.keySet()) {

            //取出在库图片的特征向量，并转成浮点数组
            List<Double> vectorDoubleList = pathMap.get(path);

            //计算相似度得分
            Double cosineSimilarity = VectorUtil.getCosineSimilarity(queryVector, vectorDoubleList);

            //原先得分
            Double preCos = scoreMap.get(path);

            //更新得分
            if (bool == 0) {
                scoreMap.replace(path, preCos + 0.5 * cosineSimilarity);
            } else if (bool == 1) {
                scoreMap.replace(path, preCos - 0.1 * cosineSimilarity);
            }

        }
    }


    public void savePathToUrlList(List<String> urlList, Map<String, Double> sortMap) {
        for (String path : sortMap.keySet()) {
            urlList.add(path);
        }
    }

    public String imgToBase64(String shot) {
        String path = "";
        if (Integer.valueOf(shot.substring(4, 9)) <= 7475) {
            path = "F:\\VBSDataset\\V3C1\\thumbnails\\" + shot.substring(4, 9) + "\\" + shot + ".png";
        } else {
            path = "F:\\VBSDataset\\V3C2\\thumbnails\\" + shot.substring(4, 9) + "\\" + shot + ".png";
        }

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


    public Map<String, Double> mapNormAndSort() {
        //（路径，得分）键值对 得分归一化
        VectorUtil.mapNormalization(scoreMap);

        //将（路径，得分）的键值对按得分降序
        return VectorUtil.sortMapByValues(scoreMap);
    }

}
