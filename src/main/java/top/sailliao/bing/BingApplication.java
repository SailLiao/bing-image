package top.sailliao.bing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author lzf
 */
@EnableScheduling
@EnableTransactionManagement
@SpringBootApplication
public class BingApplication {
    public static void main(String[] args) {
        SpringApplication.run(BingApplication.class, args);
    }
}
