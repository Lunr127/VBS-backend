package whu.vbs.Controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import whu.vbs.Service.VectorService;
import whu.vbs.utils.PathUtils;

import java.util.List;

@RestController
@RequestMapping("search")
@CrossOrigin
public class SearchController {

    @Autowired
    VectorService vectorService;

    @RequestMapping(
            value = "/text",
            method = RequestMethod.POST
    )
    @ResponseBody
    public List<String> text(@RequestBody String request) {
        JSONObject jsonObject = JSONUtil.parseObj(request);
        String textInput = jsonObject.getStr("textInput");
        String radioSelect = jsonObject.getStr("radioSelect");
        System.out.println(textInput);
        System.out.println(radioSelect);

        List<String> urlList = vectorService.searchByText(textInput);
        List<String> topList = urlList.subList(0, 100);

        return topList;
    }

    @RequestMapping(
            value = "/feedback",
            method = RequestMethod.POST
    )
    @ResponseBody
    public void feedback(@RequestBody String request) {
        JSONObject jsonObject = JSONUtil.parseObj(request);
        String path = jsonObject.getStr("path");
        System.out.println(path);
        // path = /img/shot00001_105_RKF.98dcf3e8.png
        int idByPath = vectorService.getIdByPath(PathUtils.handlePath(path));
        vectorService.positiveFeedBack(idByPath);

        System.out.println("feedback vector write success");
    }

    @RequestMapping(
            value = "/reRank",
            method = RequestMethod.POST
    )
    @ResponseBody
    public List<String> reRank(@RequestBody String request) {
        List<String> urlList = vectorService.reRankByNewQuery();
        List<String> topList = urlList.subList(0, 100);
        // int query = 1661;
        // vectorService.getGrandTruth(query, topList);
        System.out.println("reRank successfully");

        return topList;
    }

}
