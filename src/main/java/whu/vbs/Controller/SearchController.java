package whu.vbs.Controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import whu.vbs.Service.*;
import whu.vbs.utils.MultipartFileToFile;
import whu.vbs.utils.PathUtils;

import java.util.*;

@RestController
@RequestMapping("vbs")
@CrossOrigin
public class SearchController {

    @Autowired
    VectorService vectorService;

    @Autowired
    KisService kisService;

    @Autowired
    AvsService avsService;

    @Autowired
    MarineService marineService;

    @RequestMapping(
            value = "/search",
            method = RequestMethod.POST
    )
    @ResponseBody
    public String search(@RequestBody String request) {
        JSONObject jsonObject = JSONUtil.parseObj(request);
//        String textInput = jsonObject.getStr("textInput");
        String radioSelect = jsonObject.getStr("radioSelect");
        String datasetRadio = jsonObject.getStr("datasetRadio");
//        System.out.println(textInput);
        System.out.println(radioSelect);

        if (Integer.parseInt(datasetRadio) == 1) {
            if (Integer.parseInt(radioSelect) == 1) {
                return JSONUtil.toJsonStr(avsService.getInitTopK());
            }

            if ((Integer.parseInt(radioSelect) == 2) || (Integer.parseInt(radioSelect) == 3)) {
                return JSONUtil.toJsonStr(kisService.getInitTopK());
            }
        }
        if (Integer.parseInt(datasetRadio) == 2) {
            return JSONUtil.toJsonStr(marineService.getInitTopK());
        }


        return JSONUtil.toJsonStr("not success");
    }

    @RequestMapping(
            value = "/reRank",
            method = RequestMethod.POST
    )
    @ResponseBody
    public String reRank(@RequestBody String request) {
        JSONObject jsonObject = JSONUtil.parseObj(request);


//        setLikeShotsMap();
//        setNotLikeShotsMap();


        List<String> LikePaths = PathUtils.handlePathsFromWeb(jsonObject.getStr("LikePaths"));
//        System.out.println("LikePaths = " + LikePaths);
//        for (String likePath : LikePaths) {
//            System.out.print("\"" + likePath + "\"" + ", ");
//        }
//        System.out.println();
        List<String> NotLikePaths = PathUtils.handlePathsFromWeb(jsonObject.getStr("NotLikePaths"));
//        System.out.println("NotLikePaths = " + NotLikePaths);
//        for (String notLikePath : NotLikePaths) {
//            System.out.print("\"" + notLikePath + "\"" + ", ");
//        }
//        System.out.println();

        return JSONUtil.toJsonStr(avsService.reRank(LikePaths, NotLikePaths));
    }

    @RequestMapping(
            value = "/image",
            method = RequestMethod.POST
    )
    @ResponseBody
    public void image(MultipartFile file) {
        System.out.println(MultipartFileToFile.saveMultipartFile(file, "C:\\Users\\Lunr\\Desktop\\image"));
    }


    @RequestMapping(
            value = "/showVideoShot",
            method = RequestMethod.POST
    )
    @ResponseBody
    public String showVideoShot(@RequestBody String request) {
        JSONObject jsonObject = JSONUtil.parseObj(request);
        String shot = jsonObject.getStr("shot");
        String datasetRadio = jsonObject.getStr("datasetRadio");
        System.out.println(shot);
        System.out.println(datasetRadio);
        if (Integer.parseInt(datasetRadio) == 1) {
            return JSONUtil.toJsonStr(kisService.showVideoByShot(shot));
        } else if (Integer.parseInt(datasetRadio) == 2) {
            return JSONUtil.toJsonStr(marineService.showVideoByShot(shot));
        }

        return JSONUtil.toJsonStr(kisService.showVideoByShot(shot));
    }

    @RequestMapping(
            value = "/showVision1",
            method = RequestMethod.POST
    )
    @ResponseBody
    public String showVision1(@RequestBody String request) {
        JSONObject jsonObject = JSONUtil.parseObj(request);
        String shot = jsonObject.getStr("shot");

        return JSONUtil.toJsonStr(kisService.showVision1(shot));
    }

    @RequestMapping(
            value = "/getVision1",
            method = RequestMethod.POST
    )
    @ResponseBody
    public String getVision1(@RequestBody String request) {
        JSONObject jsonObject = JSONUtil.parseObj(request);
        String shot = jsonObject.getStr("shot");
        String datasetRadio = jsonObject.getStr("datasetRadio");

        if (Integer.parseInt(datasetRadio) == 1) {
            return JSONUtil.toJsonStr(kisService.getVision1(shot));
        } else if (Integer.parseInt(datasetRadio) == 2) {
            return JSONUtil.toJsonStr(marineService.getVision1(shot));
        }

        return JSONUtil.toJsonStr(kisService.getVision1(shot));
    }


    @RequestMapping(
            value = "/submit",
            method = RequestMethod.POST
    )
    @ResponseBody
    public String submit(@RequestBody String request) {
        JSONObject jsonObject = JSONUtil.parseObj(request);
        String submitUrls = jsonObject.getStr("submitUrls");
        String kisSubmitSelect = jsonObject.getStr("kisSubmitSelect");
        String radioSelect = jsonObject.getStr("radioSelect");
        String textInput = jsonObject.getStr("textInput");
        String datasetRadio = jsonObject.getStr("datasetRadio");

        int length = submitUrls.length();
        List<String> submitUrlList = Arrays.asList(submitUrls.substring(1, length - 1).split(","));

        String submitStatus = "";


        if (Integer.parseInt(datasetRadio) == 1) {
            if (Integer.parseInt(radioSelect) == 1) {
                submitStatus = avsService.submit(Integer.parseInt(textInput));
            }
            if ((Integer.parseInt(radioSelect) == 2) || (Integer.parseInt(radioSelect) == 3)) {
                if (Integer.parseInt(kisSubmitSelect) == 1) {
                    kisService.submitByVision1(submitUrlList.get(0));
                } else if (Integer.parseInt(kisSubmitSelect) == 2) {
                    kisService.submitByVision2(submitUrlList.get(0));
                }
            }
        } else if (Integer.parseInt(datasetRadio) == 2) {
            marineService.submitByVision2(submitUrlList.get(0));
        }


        return JSONUtil.toJsonStr(submitStatus);
    }


    @RequestMapping(
            value = "/submitByVision1",
            method = RequestMethod.POST
    )
    @ResponseBody
    public String submitByVision1(@RequestBody String request) {
        JSONObject jsonObject = JSONUtil.parseObj(request);
        String submitUrls = jsonObject.getStr("submitUrls");

        int length = submitUrls.length();
        List<String> submitUrlList = Arrays.asList(submitUrls.substring(1, length - 1).split(","));

        return JSONUtil.toJsonStr(kisService.submitByVision1(submitUrlList.get(0)));
    }


}
