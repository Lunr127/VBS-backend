package whu.vbs.Controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import whu.vbs.Service.VectorService;
import whu.vbs.utils.PathUtils;

import java.util.ArrayList;
import java.util.Arrays;
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
        List<String> topList = urlList.subList(0, 50);

        return topList;
    }

    @RequestMapping(
            value = "/reRank",
            method = RequestMethod.POST
    )
    @ResponseBody
    public List<String> reRank(@RequestBody String request) {
        JSONObject jsonObject = JSONUtil.parseObj(request);

        List<String> LikePaths = PathUtils.handlePathsFromWeb(jsonObject.getStr("LikePaths"));
        System.out.println("LikePaths = " + LikePaths);
        List<String> NotLikePaths = PathUtils.handlePathsFromWeb(jsonObject.getStr("NotLikePaths"));
        System.out.println("NotLikePaths = " + NotLikePaths);

        List<String> urlList = vectorService.reRank(LikePaths, NotLikePaths);
        List<String> topList = urlList.subList(0, 50);

        return topList;
    }

}
