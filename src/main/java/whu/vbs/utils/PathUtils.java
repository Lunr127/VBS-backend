package whu.vbs.utils;

public class PathUtils {
    public static String handlePath(String prePath){
        String substring = prePath.substring(5);
        String root = substring.substring(4, 9);

        String[] split = substring.split("\\.");
        String path = root + split[0] + '.' + split[2];

        return path;
    }

    public static String handleToGTPath(String prePath){
        String substring = prePath.substring(7);
        int index = substring.indexOf('_', 11);
        String path = substring.substring(1, index);

        return path;
    }
}
