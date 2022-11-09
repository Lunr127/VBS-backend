package whu.vbs.Controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import whu.vbs.Service.SearchService;
import whu.vbs.Service.VectorService;

import java.util.List;

@RestController
@RequestMapping("search")
@CrossOrigin
public class SearchController {

    @Autowired
    SearchService searchService;

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

}
