package cn.lokn.knconfig.client.config;

import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: kn property sources processor.
 * @author: lokn
 * @date: 2024/05/19 23:45
 */
// PriorityOrdered 让当前配置文件优先加载
@Data
public class PropertySourcesProcessor implements BeanFactoryPostProcessor, EnvironmentAware, PriorityOrdered {

    private final static String KN_PROPERTY_SOURCES = "KNPropertySources";
    private final static String KN_PROPERTY_SOURCE = "KNPropertySource";

    Environment environment;
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ConfigurableEnvironment env = (ConfigurableEnvironment) environment;
        if (env.getPropertySources().contains(KN_PROPERTY_SOURCES)) {
            return;
        }
        // 通过 http 请求，去knconfig-server获取配置 TODO
        Map<String, String> config = new HashMap<>();
        config.put("kn.a", "dev100");
        config.put("kn.b", "b500");
        config.put("kn.c", "c700");
        KNConfigService configService = new KNConfigServiceImpl(config);
        KNPropertySource propertySource = new KNPropertySource(KN_PROPERTY_SOURCES, configService);
        CompositePropertySource composite = new CompositePropertySource(KN_PROPERTY_SOURCE);
        composite.addPropertySource(propertySource);
        // 让其优先获取配置
        env.getPropertySources().addFirst(composite);
    }

    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }

}
