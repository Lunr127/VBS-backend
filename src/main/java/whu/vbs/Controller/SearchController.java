package whu.vbs.Controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import whu.vbs.Service.AvsService;
import whu.vbs.Service.VectorService;
import whu.vbs.utils.PathUtils;

import java.util.*;

@RestController
@RequestMapping("search")
@CrossOrigin
public class SearchController {

    Map<Integer, String[]> likeShotsMap = new HashMap<>();
    Map<Integer, String[]> notLikeShotsMap = new HashMap<>();
    Map<Integer, String> queryMap = new HashMap<>();
    public void setQueryMap(){
        queryMap.put(1661, "a hang glider floating in the sky on a sunny day");
        queryMap.put(1662, "a woman wearing sleeveless top");
        queryMap.put(1663, "a person with a tattoo on their arm");
        queryMap.put(1664, "city street where ground is covered by snow");
        queryMap.put(1665, "an adult person wearing a backpack and walking on a sidewalk");
        queryMap.put(1666, "a man wearing a blue jacket");
        queryMap.put(1667, "a person looking at themselves in a mirror");
        queryMap.put(1668, "a person wearing an apron indoors");
        queryMap.put(1669, "a woman holding a book");
        queryMap.put(1670, "a person painting on a canvas");
        queryMap.put(1671, "a man behind a pub bar or club bar");
        queryMap.put(1672, "a person wearing a cap backwards");
        queryMap.put(1673, "a man pointing with his finger");
        queryMap.put(1674, "a parachutist descending towards a field on the ground in the daytime");
        queryMap.put(1675, "two or more ducks swimming in a pond");
        queryMap.put(1676, "a white dog");
        queryMap.put(1677, "two boxers in a ring");
        queryMap.put(1678, "a man sitting on a barber chair in a shop");
        queryMap.put(1679, "a ladder with less than 6 steps");
        queryMap.put(1680, "a bow tie");
    }
    void setLikeShotsMap(){
        likeShotsMap.put(1661, new String[]{"shot00057_130", "shot00410_374", "shot00847_59", "shot01564_23", "shot03072_62"});
        likeShotsMap.put(1662, new String[]{"shot00015_42", "shot00058_106", "shot00479_95", "shot01604_103", "shot04054_48"});
        likeShotsMap.put(1663, new String[]{"shot00127_36", "shot01597_105", "shot02546_16", "shot02853_101", "shot02853_74"});
        likeShotsMap.put(1664, new String[]{"shot01613_162", "shot01613_2", "shot01745_92", "shot01887_53", "shot04096_397"});
        likeShotsMap.put(1665, new String[]{"shot00535_672", "shot01352_97", "shot01531_1", "shot03840_240", "shot04021_14"});
        likeShotsMap.put(1666, new String[]{"shot00143_30", "shot00278_69", "shot00378_39", "shot00167_201", "shot00143_5"});
        likeShotsMap.put(1667, new String[]{"shot00319_171", "shot00505_7", "shot00505_6", "shot00505_58", "shot00812_14"});
        likeShotsMap.put(1668, new String[]{"shot00152_132", "shot00181_139", "shot00585_314", "shot00918_192", "shot01512_54"});
        likeShotsMap.put(1669, new String[]{"shot00182_6", "shot00580_10", "shot00785_114", "shot01170_16", "shot01170_25"});
        likeShotsMap.put(1670, new String[]{"shot00007_25", "shot00114_36", "shot00562_75", "shot00573_123", "shot00007_88"});

    }

    void setNotLikeShotsMap(){
        notLikeShotsMap.put(1661, new String[]{"shot01507_151", "shot07168_10", "shot03340_131", "shot01146_55", "shot00615_98"});
        notLikeShotsMap.put(1662, new String[]{"shot00354_68", "shot00464_189", "shot00479_108", "shot01138_33", "shot03961_56"});
        notLikeShotsMap.put(1663, new String[]{"shot00342_11", "shot00342_9", "shot01380_22", "shot01743_100", "shot02768_80"});
        notLikeShotsMap.put(1664, new String[]{"shot01237_155", "shot05262_16", "shot06839_5", "shot07168_77", "shot06775_135"});
        notLikeShotsMap.put(1665, new String[]{"shot00158_50", "shot00464_14", "shot00750_635", "shot00896_139", "shot02499_118"});
        notLikeShotsMap.put(1666, new String[]{"shot00181_75", "shot00378_166", "shot00347_75", "shot00332_32", "shot00248_22"});
        notLikeShotsMap.put(1667, new String[]{"shot00015_11", "shot00028_11", "shot00036_514", "shot00036_516", "shot00036_524"});
        notLikeShotsMap.put(1668, new String[]{"shot00036_48", "shot00054_1", "shot00197_41", "shot00193_604", "shot00743_42"});
        notLikeShotsMap.put(1669, new String[]{"shot00217_187", "shot00669_51", "shot00056_119", "shot00087_129", "shot00159_86"});
        notLikeShotsMap.put(1670, new String[]{"shot00573_26", "shot00573_295", "shot00389_439", "shot00056_151", "shot00114_39"});
    }

    int query;






    @Autowired
    VectorService vectorService;

    @Autowired
    AvsService avsService;

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


//        setQueryMap();
//        for (Integer key: queryMap.keySet()){
//            if (textInput.equals(queryMap.get(key))) {
//                query = key;
//            }
//        }

//        List<String> urlList = vectorService.searchByText(textInput);
//        List<String> topList = urlList.subList(0, 50);

        return JSONUtil.toJsonStr(avsService.topKTest(textInput));
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

}
