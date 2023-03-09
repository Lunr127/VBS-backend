package whu.vbs.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

public class PathUtils {
    public static String handlePath(String prePath) {
        String substring = prePath.substring(5);
        String root = substring.substring(4, 9);

        String[] split = substring.split("\\.");
        String path = '/' + root + '/' + split[0] + '.' + split[2];

        return path;
    }

    public static String handleToGTPath(String prePath) {
        String substring = prePath.substring(prePath.indexOf('s') - 1);
        int index = substring.indexOf('_', 11);
        String path = substring.substring(1, index);

        return path;
    }

    public static List<String> handlePathsFromWeb(String paths) {
        List<String> pathList = Arrays.asList(paths.substring(paths.indexOf('[') + 1, paths.indexOf(']')).split(","));

        for (int i = 0; i < pathList.size(); i++) {
            if (pathList.get(0).length() < 5) {
                break;
            }
            String path = pathList.get(i);
            path = path.substring(1, path.length() - 1);
            pathList.set(i, path);
        }

        return pathList;
    }

    public static String marinePathIdToFrameId(String pathId){
        int index = pathId.indexOf(".");
        String frameId = pathId.substring(0, index);
        frameId = frameId.replace("/", "_");
        return frameId;
    }


    public static void savePathToUrlList(List<String> urlList, Map<String, Double> sortMap) {
        for (String path : sortMap.keySet()) {
            urlList.add(path);
        }
    }


    public static List<Map<String, String>> urlToBase64List(List<String> urlList, int showTopK){
        List<Map<String, String>> base64List = new ArrayList<>();
        for (String shot : urlList.subList(0, showTopK)) {
            Map<String, String> base64Map = new HashMap<>();// (base64，路径)键值对
            String base64 = "data:image/png;base64," + imgToBase64(shot);
            base64Map.put("shot", shot);
            base64Map.put("base64", base64);
            base64List.add(base64Map);
        }
        return base64List;
    }

    public static String imgToBase64(String shot) {
        String path = "";
        if (Integer.parseInt(shot.substring(4, 9)) <= 7475) {
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

    public static List<Map<String, String>> marineUrlToBase64List(List<String> urlList, int showTopK){
        List<Map<String, String>> base64List = new ArrayList<>();
        for (String shot : urlList.subList(0, showTopK)) {
            Map<String, String> base64Map = new HashMap<>();// (base64，路径)键值对

            String path = "F:\\VBSDataset\\Marine_thumbnails\\";
            String[] shotSplit = shot.split("_");
            path = path + shotSplit[0] + "_" + shotSplit[1] + "\\" + shotSplit[2] + "\\" + shotSplit[3] + ".jpg";

            File file = new File(path);
            if (!file.exists()){
                continue;
            }

            String base64 = "data:image/png;base64," + marineImgToBase64(shot);
            base64Map.put("shot", shot);
            base64Map.put("base64", base64);
            base64List.add(base64Map);
        }
        return base64List;
    }

    public static String marineImgToBase64(String shot) {
        String path = "F:\\VBSDataset\\Marine_thumbnails\\";

        // Ambon_Apr2012_0001_1
        String[] shotSplit = shot.split("_");

        path = path + shotSplit[0] + "_" + shotSplit[1] + "\\" + shotSplit[2] + "\\" + shotSplit[3] + ".jpg";

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
}
