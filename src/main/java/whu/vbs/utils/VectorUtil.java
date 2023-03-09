package whu.vbs.utils;

import cn.hutool.core.convert.Convert;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import java.util.*;
import java.util.stream.Collectors;

public class VectorUtil {
    public static List<Double> strToDouble(String str, int sub) {
        int length = str.length();
        str = str.substring(sub, length - sub);
        ArrayList<String> strings = new ArrayList<>(Arrays.asList(str.split("\\s+")));
        strings.remove("]]");
        ArrayList<Double> doubleArrayList = new ArrayList<>(Arrays.asList(Convert.toDoubleArray(strings)));
        if (doubleArrayList.get(0) == null) {
            doubleArrayList.remove(0);
        }
        return doubleArrayList;
    }

    public static List<Double> queryStrToDouble(String str){
        int length = str.length();
        str = str.substring(9, length - 3);
        ArrayList<String> strings = new ArrayList<>(Arrays.asList(str.split(",")));
        ArrayList<Double> doubleArrayList = new ArrayList<>(Arrays.asList(Convert.toDoubleArray(strings)));
        if (doubleArrayList.get(0) == null) {
            doubleArrayList.remove(0);
        }
        return doubleArrayList;
    }


    public static List<Double> avsQueryStrToDouble(String str){
        int length = str.length();
        str = str.substring(9, length - 20);
        ArrayList<String> strings = new ArrayList<>(Arrays.asList(str.split(",")));
        ArrayList<Double> doubleArrayList = new ArrayList<>(Arrays.asList(Convert.toDoubleArray(strings)));
        if (doubleArrayList.get(0) == null) {
            doubleArrayList.remove(0);
        }
        return doubleArrayList;
    }

    public static List<Double> imageStrToDouble(String str){
        int length = str.length();
        int begin = str.indexOf("[");
        int end = str.indexOf("]");
        str = str.substring(begin+2, end);
        ArrayList<String> strings = new ArrayList<>(Arrays.asList(str.split(",")));
        ArrayList<Double> doubleArrayList = new ArrayList<>(Arrays.asList(Convert.toDoubleArray(strings)));
        if (doubleArrayList.get(0) == null) {
            doubleArrayList.remove(0);
        }
        return doubleArrayList;
    }


    public static List<Double> marineStrToDouble(String str){
        int begin = str.indexOf("[");
        int end = str.indexOf("]");
        str = str.substring(begin+2, end);
        ArrayList<String> strings = new ArrayList<>(Arrays.asList(str.split(",")));
        ArrayList<Double> doubleArrayList = new ArrayList<>(Arrays.asList(Convert.toDoubleArray(strings)));
        if (doubleArrayList.get(0) == null) {
            doubleArrayList.remove(0);
        }
        return doubleArrayList;
    }


    /**
     * 得到两个向量的余弦相似度
     * @param vectorAs
     * @param vectorBs
     * @return
     */
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


    /**
     * 根据 map 的值排序
     * 不改变传入 map 的顺序
     * @param map
     * @return
     * @param <K>
     * @param <V>
     */
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


    /**
     * 将 map 的值做归一化
     * @param map
     */
    public static void mapNormalization(Map<String, Double> map) {

        double min = 1.0;
        double max = 0.0;

        for (String key: map.keySet()) {
            double cos = map.get(key);
            if (cos < min){
                min = cos;
            }
            if (cos > max){
                max = cos;
            }
        }
        double dValue = max - min;

        for (String key: map.keySet()) {
            Double oldValue = map.get(key);
            map.replace(key, (oldValue-min)/(dValue));
        }
    }


    public static double getPearsonsCorrelation(List<Double> vectorAs, List<Double> vectorBs){
        PearsonsCorrelation p = new PearsonsCorrelation();
        Double[] xD = new Double[vectorBs.size()];
        vectorAs.toArray(xD);
        double[] x = new double[xD.length];
        for (int i = 0; i < xD.length; i++){
            x[i] = xD[i];
        }

        Double[] yD = new Double[vectorBs.size()];
        vectorAs.toArray(yD);
        double[] y = new double[yD.length];
        for (int i = 0; i < yD.length; i++){
            y[i] = yD[i];
        }

        return p.correlation(x, y);
    }

    public static double getDot(List<Double> vectorAs, List<Double> vectorBs){
        int index = 0;
        double sum = 0;
        for (double vectorA : vectorAs) {
            sum = sum + vectorA * vectorBs.get(index);
            index++;
        }
        return sum;
    }

}
