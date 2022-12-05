package whu.vbs.utils;

import java.util.Arrays;
import java.util.List;

public class PathUtils {
    public static String handlePath(String prePath){
        String substring = prePath.substring(5);
        String root = substring.substring(4, 9);

        String[] split = substring.split("\\.");
        String path = '/' + root + '/' + split[0] + '.' + split[2];

        return path;
    }

    public static String handleToGTPath(String prePath){
        String substring = prePath.substring(7);
        int index = substring.indexOf('_', 11);
        String path = substring.substring(1, index);

        return path;
    }

    public static List<String> handlePathsFromWeb(String paths){
        List<String> pathList = Arrays.asList(paths.substring(paths.indexOf('[') + 1, paths.indexOf(']')).split(","));

        for (int i = 0; i < pathList.size(); i++){
            if (pathList.get(0).length() < 5){
                break;
            }
            String path = pathList.get(i);
            path = path.substring(1, path.length() - 1);
            pathList.set(i, path);
        }

        return pathList;
    }
}
