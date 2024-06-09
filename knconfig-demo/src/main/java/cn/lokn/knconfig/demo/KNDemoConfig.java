package cn.lokn.knconfig.demo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description: Test demo configs
 * @author: lokn
 * @date: 2024/05/19 00:00
 */
@Data
@ConfigurationProperties(prefix = "kn")
public class KNDemoConfig {
    String a;
    String b;
}
