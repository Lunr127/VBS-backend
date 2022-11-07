package whu.vbs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("whu.vbs.Mapper")
public class VbsApplication {

    public static void main(String[] args) {
        SpringApplication.run(VbsApplication.class, args);
    }

}
