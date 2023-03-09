package whu.vbs.Service;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.tomcat.util.http.fileupload.FileUtils;
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
import whu.vbs.Entity.CsvFile.CsvTest;
import whu.vbs.Entity.CsvFile.MarineFrameBoundary;
import whu.vbs.Entity.CsvFile.MarineVector;
import whu.vbs.Entity.CsvFile.MasterShotBoundary;
import whu.vbs.Entity.VectorResult;
import whu.vbs.Mapper.MarineFrameBoundaryMapper;
import whu.vbs.Mapper.MarineVectorMapper;
import whu.vbs.utils.PathUtils;
import whu.vbs.utils.VectorUtil;

import java.io.*;
import java.net.ConnectException;
import java.nio.file.Files;
import java.util.*;

@Service
public class MarineService {
    @Autowired
    MarineVectorMapper marineVectorMapper;

    @Autowired
    MarineFrameBoundaryMapper marineFrameBoundaryMapper;

    List<Double> queryVector = new ArrayList<>();

    String submitVideoId = "";

    int submitTime;

    String showVideoId = "";


    Map<String, Double> vision1ScoreMap = new HashMap<>();//（路径，得分）键值对

    Map<String, Double> videoScoreMap = new HashMap<>();//（路径，得分）键值对

    Map<String, Double> scoreMap = new HashMap<>();//（路径，得分）键值对: 形如（shot00001_1, 0.8）

    Map<String, List<Double>> marineVectorMap = new HashMap<>(); // (关键帧编号，向量键值对) 形式如下(Ambon_Apr2012_0001_1, vector)

    List<String> urlList = new ArrayList<>();//查询结果的路径


    public List<Map<String, String>> getInitTopK() {

        int showTopK = 500;
        scoreMap = new HashMap<>();

        // 获得数据库中marine_vector表所有内容
        List<MarineVector> marineVectors = marineVectorMapper.selectList(null);
        for (MarineVector marineVector : marineVectors) {
            // 转化id的表示形式 Ambon_Apr2012/0001/1.png ---> Ambon_Apr2012_0001_1
            String id = PathUtils.marinePathIdToFrameId(marineVector.getId());

            // 获得vector并转化成数组
            String strVector = marineVector.getVector();
            List<Double> vector = VectorUtil.marineStrToDouble(strVector);

            // 放入键值对中
            marineVectorMap.put(id, vector);
        }

        // 得到测试图片的向量
//        String imagePath = "D:/Download/VBSDataset/VBS_task/kis-v/query.png";
//        queryVector = getImageVector(imagePath);
//        System.out.println(queryVector);
        cn.hutool.core.io.file.FileReader fileReader = new FileReader("D:\\Download\\VBSDataset\\VBS_task\\Init_top10000\\query.txt");
        String queryFile = fileReader.readString();
        queryVector = VectorUtil.strToDouble(queryFile, 2);
        System.out.println(queryVector);

        // 对每个在库帧 得到与查询图片的特征相似度
        for (String shot : marineVectorMap.keySet()) {

            List<Double> vector = marineVectorMap.get(shot);

            //计算查询和图片的相似度得分
            Double cosineSimilarity = VectorUtil.getCosineSimilarity(queryVector, vector);

            //建立（路径，得分）的键值对
            scoreMap.put(shot, cosineSimilarity);
        }

        // 得分归一化
        VectorUtil.mapNormalization(scoreMap);

        // 将（路径，得分）的键值对按得分降序
        Map<String, Double> sortMap = VectorUtil.sortMapByValues(scoreMap);

        urlList = new ArrayList<>();//查询结果的路径
        //将路径存入urlList
        savePathToUrlList(urlList, sortMap);

        PathUtils.savePathToUrlList(urlList, sortMap);

//        submitLog();

        return PathUtils.marineUrlToBase64List(urlList, showTopK);
    }

    public List<Map<String, String>> showVision1(String shot) {

        submitVideoId = shot.substring(4, 9);

        int showTopK = 20;
        String path = "D:\\Download\\VBSDataset\\VBS_task\\Segment_frames\\info.csv";

        vision1ScoreMap = new HashMap<>();

        CsvReader reader = CsvUtil.getReader();
        List<CsvTest> result = reader.read(ResourceUtil.getUtf8Reader(path), CsvTest.class);
        for (CsvTest csvTest : result) {
            String id = csvTest.getId();
            int index = id.indexOf("j");
            id = id.substring(index - 4, index - 1);
            List<Double> vectorDoubleList = VectorUtil.imageStrToDouble(csvTest.getVector());

            //计算查询和图片的相似度得分
            Double cosineSimilarity = VectorUtil.getCosineSimilarity(queryVector, vectorDoubleList);

            vision1ScoreMap.put(id, cosineSimilarity);
        }

        VectorUtil.mapNormalization(vision1ScoreMap);

        //将（路径，得分）的键值对按得分降序
        Map<String, Double> sortMap = VectorUtil.sortMapByValues(vision1ScoreMap);

        List<String> urlList = new ArrayList<>();//查询结果的路径
        //将路径存入urlList
        PathUtils.savePathToUrlList(urlList, sortMap);

        showTopK = Math.min(urlList.size(), showTopK);

        return v1UrlToBase64List(urlList, showTopK);
    }

    public String getVision1(String shot) {

        try {
            File directory = new File("D:\\Download\\VBSDataset\\VBS_task\\Segment_frames");
            FileUtils.deleteDirectory(directory);
        }catch (IOException e){
            e.printStackTrace();
        }

        String[] shotSplit = shot.split("_");
        String videoId = shotSplit[0] + "_" + shotSplit[1] + "_" + shotSplit[2];
        String shotId = shotSplit[3];
        System.out.println(videoId);
        System.out.println(shotId);

        List<MarineFrameBoundary> marineFrameBoundaryList = marineFrameBoundaryMapper.selectList(null);
        String time = "";
        for (MarineFrameBoundary marineFrameBoundary : marineFrameBoundaryList) {
            if (Objects.equals(marineFrameBoundary.getFrameId(), shot)) {
                time = marineFrameBoundary.getTime();
            }
        }
        submitVideoId = videoId;
        submitTime = Integer.parseInt(time);
        System.out.println(time);

        return time;
    }

    public List<Map<String, String>> showVideoByShot(String shot) {

        List<String> videoShotList = new ArrayList<>();

        String[] shotSplit = shot.split("_");
        String videoId = shotSplit[0] + "_" + shotSplit[1] + "_" + shotSplit[2];
        System.out.println(videoId);

        for (String key: marineVectorMap.keySet()) {
            if (videoId.length() > key.length()){
                continue;
            }
            if (videoId.equals(key.substring(0, videoId.length()))){
                videoShotList.add(key);
            }
        }

        List<Double> shotVector = marineVectorMap.get(shot);

        for (String videoShot : videoShotList) {
            List<Double> vector = marineVectorMap.get(videoShot);

            //计算查询和图片的相似度得分
            Double cosineSimilarity = VectorUtil.getCosineSimilarity(shotVector, vector);

            //建立（路径，得分）的键值对
            videoScoreMap.put(videoShot, cosineSimilarity);
        }


        VectorUtil.mapNormalization(videoScoreMap);

        //将（路径，得分）的键值对按得分降序
        Map<String, Double> sortMap = VectorUtil.sortMapByValues(videoScoreMap);

        List<String> urlList = new ArrayList<>();//查询结果的路径
        //将路径存入urlList
        PathUtils.savePathToUrlList(urlList, sortMap);

        videoScoreMap.clear();
        sortMap.clear();

        return PathUtils.marineUrlToBase64List(urlList, urlList.size());
    }

    public String submitByVision2(String shot){

        String[] shotSplit = shot.split("_");
        String videoId = shotSplit[0] + "_" + shotSplit[1] + "_" + shotSplit[2];
        int segment = Integer.parseInt(shotSplit[3].substring(0, shotSplit[3].length() - 1));
        submitVideoId = videoId.substring(1);
        System.out.println(submitVideoId);

        submitTime = 2 * segment;
        int second = submitTime;
        int HH = second / 3600;
        int MM = second / 60 - 60 * HH;
        int SS = second - 60 * MM;

        String strHH = HH < 10 ? "0" + HH : String.valueOf(HH);
        String strMM = MM < 10 ? "0" + MM : String.valueOf(MM);
        String strSS = SS < 10 ? "0" + SS : String.valueOf(SS);
        String strFF = "00";
        String timeCode = strHH + ":" + strMM + ":" + strSS + ":" + strFF;

        String submitApi = "Submit Time: " + submitVideoId + " " + strHH + ":" + strMM + ":" + strSS + ":" + strFF;
        submit(submitVideoId, timeCode);
        return submitApi;
    }


    public void submit(String videoId, String submitTime){
        System.out.println(videoId + " " + submitTime);

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

        System.out.println("login successful");
        System.out.println("user: " + login.getUsername());
        System.out.println("role: " + login.getRole().getValue());
        System.out.println("session: " + login.getSessionId());

        //store session token for future requests
        String sessionId = login.getSessionId();

//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException ignored) {
//        }

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

        if (submissionResponse != null && submissionResponse.getStatus()) {
            System.out.println("The submission was successfully sent to the server.");


            List<QueryResult> queryResultList = new ArrayList<>();

            int rankIndex = 1;
            for (String shot : urlList) {
                String[] shotSplit = shot.split("_");
                String itemName = shotSplit[0] + "_" + shotSplit[1] + "_" + shotSplit[2];
                Integer segment = Integer.valueOf(shotSplit[3]);
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
//                                .results(
//                                        List.of(
//                                                new QueryResult().item("some_item_name").segment(3).score(0.9).rank(1),
//                                                new QueryResult().item("some_item_name").segment(5).score(0.85).rank(2),
//                                                new QueryResult().item("some_other_item_name").segment(12).score(0.76).rank(3)
//                                        )
//                                )
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
            Thread.sleep(1000); //doing other things...
        } catch (InterruptedException ignored) {
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




    public List<Map<String, String>> v1UrlToBase64List(List<String> urlList, int showTopK){
        List<Map<String, String>> base64List = new ArrayList<>();
        for (String shot : urlList.subList(0, showTopK)) {
            Map<String, String> base64Map = new HashMap<>();// (base64，路径)键值对
            String base64 = "data:image/png;base64," + v1ImgToBase64(shot);
            base64Map.put("shot", shot);
            base64Map.put("base64", base64);
            base64List.add(base64Map);
        }
        return base64List;
    }

    public String v1ImgToBase64(String shot) {
        String path = "D:\\Download\\VBSDataset\\VBS_task\\Segment_frames\\" + shot + ".jpg";

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

    List<Double> getImageVector(String imagePath) {
        //调用 python 函数得到图片的特征向量
        String[] args1 = new String[]{"E:\\Git\\lavis2\\venv\\Scripts\\python.exe", "E:\\Git\\lavis2\\imageExtractor.py", imagePath};
        String imageVector = runPython(args1);
        List<Double> imageVectorList = VectorUtil.imageStrToDouble(imageVector);
        return imageVectorList;
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


    public void savePathToUrlList(List<String> urlList, Map<String, Double> sortMap) {
        for (String path : sortMap.keySet()) {
            urlList.add(path);
        }
    }
}
