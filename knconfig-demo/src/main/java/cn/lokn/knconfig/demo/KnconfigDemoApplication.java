package cn.lokn.knconfig.demo;

import cn.lokn.knconfig.client.annotation.EnableKnConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableConfigurationProperties({KNDemoConfig.class})
@EnableKnConfig
@RestController
public class KnconfigDemoApplication {

    @Value("${kn.a}")
    private String a;

    @Value("${kn.b}")
    private String b;

    @Autowired
    private KNDemoConfig kndemoConfig;

    public static void main(String[] args) {
        SpringApplication.run(KnconfigDemoApplication.class, args);
    }

    @GetMapping("/demo")
    public String demo() {
        return "kn.a = " + a + "\n"
                + "kn.b = " + b + "\n"
                + "demo.a = " + kndemoConfig.getA() + "\n"
                + "demo.b = " + kndemoConfig.getB() + "\n";
    }

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            System.out.println(a);
            System.out.println(kndemoConfig.getA());
        };
    }

}
