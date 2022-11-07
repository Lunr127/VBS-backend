package whu.vbs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import whu.vbs.utils.Milvus;

@SpringBootTest
class VbsApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void queryTest(){
        Milvus.query();
    }

}
