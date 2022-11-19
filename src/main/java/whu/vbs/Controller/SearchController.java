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
    public List<String> feedback(@RequestBody String request) {
        JSONObject jsonObject = JSONUtil.parseObj(request);
        String path = jsonObject.getStr("path");
        int idByPath = vectorService.getIdByPath(PathUtils.handlePath(path));
        List<String> urlList = vectorService.positiveFeedBack(idByPath);
        List<String> topList = urlList.subList(0, 100);

        return topList;
    }

}
