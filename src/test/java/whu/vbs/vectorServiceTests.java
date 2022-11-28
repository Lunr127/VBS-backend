package whu.vbs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import whu.vbs.Service.VectorService;

import java.util.List;

@SpringBootTest
public class vectorServiceTests {

    @Autowired
    VectorService vectorService;

    @Test
    void test1(){

        String query = "a hang glider floating in the sky on a sunny day";

        List<String> urlList = vectorService.searchByText(query);
        List<String> topList = urlList.subList(0, 50);
        System.out.println(topList);
    }

}
