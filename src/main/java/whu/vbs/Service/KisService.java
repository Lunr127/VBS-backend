package whu.vbs.Service;

import cn.hutool.core.io.FileUtil;
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
import whu.vbs.Entity.CsvFile.GrandTruthResult;
import whu.vbs.Entity.CsvFile.KisvQuery;
import whu.vbs.Entity.CsvFile.MasterShotBoundary;
import whu.vbs.Entity.VectorResult;
import whu.vbs.Mapper.KistQueryMapper;
import whu.vbs.Mapper.KisvQueryMapper;
import whu.vbs.Mapper.MasterShotBoundaryMapper;
import whu.vbs.Mapper.VectorMapper;
import whu.vbs.utils.PathUtils;
import whu.vbs.utils.VectorUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.nio.file.Files;
import java.util.*;

@Service
public class KisService {

    @Autowired
    KisvQueryMapper kisvQueryMapper;

    @Autowired
    KistQueryMapper kistQueryMapper;

    @Autowired
    MasterShotBoundaryMapper masterShotBoundaryMapper;

    @Autowired
    VectorMapper vectorMapper;


    Map<String, Double> scoreMap = new HashMap<>();//（路径，得分）键值对
    Map<String, Double> videoScoreMap = new HashMap<>();//（路径，得分）键值对

    Map<String, Double> vision1ScoreMap = new HashMap<>();//（路径，得分）键值对

    List<Double> queryVector = new ArrayList<>();

    List<String> urlList = new ArrayList<>();

    int query = 2;

    double startTime;
    double endTime;

    String showVideoId = "";

    String submitVideoId = "";

    public List<Map<String, String>> getInitTopK() {

        int showTopK = 500;
        scoreMap = new HashMap<>();

//        CsvReader reader = CsvUtil.getReader();
//        String csvPath = "D:\\Download\\VBSDataset\\kisv_top10000_1\\" + query + ".csv";
//        List<GrandTruthResult> resultList = reader.read(ResourceUtil.getUtf8Reader(csvPath), GrandTruthResult.class);
//        // 获取数据库中查询对应的特征向量
//        QueryWrapper<KisvQuery> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("query_id", query);
//        KisvQuery kisvQuery = kisvQueryMapper.selectOne(queryWrapper);
//        queryVector = VectorUtil.strToDouble(kisvQuery.getVector(), 1);

        CsvReader reader = CsvUtil.getReader();
        String csvPath = "D:\\Download\\VBSDataset\\VBS_task\\Init_top10000\\init.csv";
        List<GrandTruthResult> resultList = reader.read(ResourceUtil.getUtf8Reader(csvPath), GrandTruthResult.class);

        FileReader fileReader = new FileReader("D:\\Download\\VBSDataset\\VBS_task\\Init_top10000\\query.txt");
        String queryFile = fileReader.readString();
        queryVector = VectorUtil.strToDouble(queryFile, 2);

        for (GrandTruthResult grandTruthResult : resultList) {
            //特征向量，并转成浮点数组
            List<Double> vectorDoubleList = VectorUtil.strToDouble(grandTruthResult.getVector(), 1);

            //计算查询和图片的相似度得分
            Double cosineSimilarity = VectorUtil.getCosineSimilarity(queryVector, vectorDoubleList);

            //建立（路径，得分）的键值对
            scoreMap.put(PathUtils.handleToGTPath(grandTruthResult.getShot()), cosineSimilarity);
        }

        //得分归一化
        VectorUtil.mapNormalization(scoreMap);

        //将（路径，得分）的键值对按得分降序
        Map<String, Double> sortMap = VectorUtil.sortMapByValues(scoreMap);

        urlList = new ArrayList<>();//查询结果的路径
        //将路径存入urlList
        PathUtils.savePathToUrlList(urlList, sortMap);

        return PathUtils.urlToBase64List(urlList, showTopK);
    }


    public List<Map<String, String>> showVideoByShot(String shot) {

        int showTopK = 200;
        videoScoreMap = new HashMap<>();

        showVideoId = shot.substring(4, 9);
        submitVideoId = shot.substring(4, 9);

        QueryWrapper<VectorResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("video_id", showVideoId);
        List<VectorResult> vectorResultList = vectorMapper.selectList(queryWrapper);

        for (VectorResult vectorResult : vectorResultList) {
            String path = vectorResult.getPath();
            List<Double> vectorDoubleList = VectorUtil.strToDouble(vectorResult.getVector(), 1);

            //计算查询和图片的相似度得分
            Double cosineSimilarity = VectorUtil.getCosineSimilarity(queryVector, vectorDoubleList);

            videoScoreMap.put(path, cosineSimilarity);
        }

        VectorUtil.mapNormalization(videoScoreMap);

        //将（路径，得分）的键值对按得分降序
        Map<String, Double> sortMap = VectorUtil.sortMapByValues(videoScoreMap);

        List<String> urlList = new ArrayList<>();//查询结果的路径
        //将路径存入urlList
        PathUtils.savePathToUrlList(urlList, sortMap);

        showTopK = Math.min(urlList.size(), showTopK);

        videoScoreMap.clear();
        sortMap.clear();

        return PathUtils.urlToBase64List(urlList, showTopK);
    }


    public List<Map<String, String>> showVision1(String shot) {

        String cmd = "cmd /c start D:\\Download\\psftp\\upload_image_from_supermicro.bat";
        try {
            Process ps = Runtime.getRuntime().exec(cmd);
            ps.waitFor();
        } catch (IOException | InterruptedException ioe) {
            ioe.printStackTrace();
        }
        System.out.println("download segment successfully");

        submitVideoId = shot.substring(4, 9);

        try{
            Thread.sleep(1000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        int showTopK = 20;
        String path = "D:\\Download\\VBSDataset\\VBS_task\\Segment_frames\\info.csv";

        while (!FileUtil.exist(path)){
            try{
                Thread.sleep(500);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }

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
            FileUtils.cleanDirectory(directory);
        }catch (IOException e){
            e.printStackTrace();
        }

        String videoId = shot.substring(4, 9);
        String shotId = shot.substring(shot.indexOf("_") + 1);

        QueryWrapper<MasterShotBoundary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("video_id", videoId);
        List<MasterShotBoundary> boundaryList = masterShotBoundaryMapper.selectList(queryWrapper);
        MasterShotBoundary boundary = boundaryList.get(Integer.parseInt(shotId) - 1);

        startTime = Double.parseDouble(boundary.getStartTime()) * 1000;
        endTime = Double.parseDouble(boundary.getEndTime()) * 1000;

        String startTimeStr = String.valueOf(startTime);
        startTimeStr = startTimeStr.substring(0, startTimeStr.indexOf("."));

        String endTimeStr = String.valueOf(endTime);
        endTimeStr = endTimeStr.substring(0, endTimeStr.indexOf("."));


        String vision1Info = videoId + " " + startTimeStr + " " + endTimeStr;
        System.out.println(vision1Info);

        return vision1Info;
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

    public String submitByVision1(String shotId){
        shotId = shotId.substring(1, shotId.length() - 1);
        int index = Integer.parseInt(shotId);
        double submitTime = startTime + 500 * (index - 2);
        int second = (int) (submitTime / 1000);
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

    public String submitByVision2(String shot){
        shot = shot.substring(1, shot.length() - 1);
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


}
