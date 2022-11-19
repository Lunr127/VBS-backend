package whu.vbs.utils;

import cn.hutool.core.convert.Convert;

import java.util.*;
import java.util.stream.Collectors;

public class VectorUtil {
    public static List<Double> strToDouble(String str, int sub) {
        int length = str.length();
        str = str.substring(sub, length - sub);
        ArrayList<String> strings = new ArrayList<>(Arrays.asList(str.split("\\s+")));
        ArrayList<Double> doubleArrayList = new ArrayList<>(Arrays.asList(Convert.toDoubleArray(strings)));
        if (doubleArrayList.get(0) == null) {
            doubleArrayList.remove(0);
        }
        return doubleArrayList;
    }


    public static double getCosineSimilarity(List<Double> vectorAs, List<Double> vectorBs) {
        double dividend = 0;
        double divisorX = 0;
        double divisorY = 0;
        double result;
        int index = 0;
        for (double vectorA : vectorAs) {
            dividend = dividend + vectorA * vectorBs.get(index);
            divisorX = divisorX + Math.pow(vectorA, 2);
            divisorY = divisorY + Math.pow(vectorBs.get(index), 2);
            index++;
        }
        result = dividend / (Math.sqrt(divisorX) * Math.sqrt(divisorY));
        return result;
    }


    public static <K extends Comparable, V extends Comparable> Map<K, V> sortMapByValues(Map<K, V> map) {
        //需要用LinkedHashMap排序
        HashMap<K, V> finalMap = new LinkedHashMap<K, V>();
        //取出map键值对Entry<K,V>，然后按照值排序，最后组成一个新的列表集合
        List<Map.Entry<K, V>> list = map.entrySet()
                .stream()
                //sorted((p2,p1)   表示由大到小排序   ||  sorted((p1,p2)   表示由小到大排序
                .sorted((p2, p1) -> p1.getValue().compareTo(p2.getValue()))
                .collect(Collectors.toList());
        //遍历集合，将排好序的键值对Entry<K,V>放入新的map并返回。
        list.forEach(ele -> finalMap.put(ele.getKey(), ele.getValue()));
        return finalMap;
    }


    public static void normalization(List<Double> list) {
        List<Double> tmpList = list;
        Collections.sort(tmpList);
        Double min = tmpList.get(0);
        Double max = tmpList.get(tmpList.size()-1);
        double dValue = max - min;
        list.replaceAll(i -> (i - min) / (dValue));
    }
}
