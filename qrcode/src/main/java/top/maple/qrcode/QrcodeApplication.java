package top.maple.qrcode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author creted by wangjg on 2019/11/14 14:43
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class QrcodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(QrcodeApplication.class, args);
    }

}
