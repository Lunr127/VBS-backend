package whu.vbs.Controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import whu.vbs.Service.SearchService;

@RestController
@RequestMapping("search")
@CrossOrigin
public class SearchController {

    @Autowired
    SearchService searchService;

    @RequestMapping(
            value = "/text",
            method = RequestMethod.POST
    )
    @ResponseBody
    public String text(@RequestBody String request) {
        JSONObject jsonObject = JSONUtil.parseObj(request);
        String textInput = jsonObject.getStr("textInput");
        String radioSelect = jsonObject.getStr("radioSelect");

        System.out.println(textInput);
        System.out.println(radioSelect);

        return textInput;
    }

}
