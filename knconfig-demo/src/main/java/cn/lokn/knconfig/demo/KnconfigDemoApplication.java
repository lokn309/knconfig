package cn.lokn.knconfig.demo;

import cn.lokn.knconfig.client.annotation.EnableKnConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties({KNDemoConfig.class})
@EnableKnConfig
public class KnconfigDemoApplication {

    @Value("${kn.a}")
    private String a;

    @Autowired
    private KNDemoConfig kndemoConfig;

    public static void main(String[] args) {
        SpringApplication.run(KnconfigDemoApplication.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            System.out.println(a);
            System.out.println(kndemoConfig.getA());
        };
    }

}
