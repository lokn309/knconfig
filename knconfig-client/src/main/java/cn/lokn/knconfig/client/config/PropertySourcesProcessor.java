package cn.lokn.knconfig.client.config;

import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
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
public class PropertySourcesProcessor implements BeanFactoryPostProcessor, ApplicationContextAware, EnvironmentAware, PriorityOrdered {

    private final static String KN_PROPERTY_SOURCES = "KNPropertySources";
    private final static String KN_PROPERTY_SOURCE = "KNPropertySource";
    ApplicationContext applicationContext;
    Environment environment;
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ConfigurableEnvironment ENV = (ConfigurableEnvironment) environment;
        if (ENV.getPropertySources().contains(KN_PROPERTY_SOURCES)) {
            return;
        }
        // 通过 http 请求，去knconfig-server获取配置
        String app = ENV.getProperty("knconfig.app", "app1");
        String env = ENV.getProperty("knconfig.env", "dev");
        String ns = ENV.getProperty("knconfig.ns", "public");
        String configServer = ENV.getProperty("knconfig.configServer", "http://localhost:9129");

        ConfigMeta configMeta = new ConfigMeta(app, env, ns ,configServer);

        KNConfigService configService = KNConfigService.getDefault(applicationContext, configMeta);
        KNPropertySource propertySource = new KNPropertySource(KN_PROPERTY_SOURCES, configService);
        CompositePropertySource composite = new CompositePropertySource(KN_PROPERTY_SOURCE);
        composite.addPropertySource(propertySource);
        // 让其优先获取配置
        ENV.getPropertySources().addFirst(composite);
    }

    @Override
    public int getOrder() {
        // 设置最高优先级，越小优先级越高
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }

}
