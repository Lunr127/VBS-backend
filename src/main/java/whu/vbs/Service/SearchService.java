package whu.vbs.Service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class SearchService {

    public String test(){
        Process proc;
        try {
            proc = Runtime.getRuntime().exec("python C:\\Users\\Lunr\\Desktop\\test\\ptest.py");//执行Py文件
            //用输入输出流来截取结果
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            proc.waitFor();
            return "success";
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return e.toString();
        }

    }

}
