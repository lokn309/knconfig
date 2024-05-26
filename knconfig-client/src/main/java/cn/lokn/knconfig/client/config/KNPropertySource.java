package cn.lokn.knconfig.client.config;

import org.springframework.core.env.EnumerablePropertySource;

/**
 * @description: KN property source
 * @author: lokn
 * @date: 2024/05/19 22:44
 */
public class KNPropertySource<T> extends EnumerablePropertySource<KNConfigService> {

    public KNPropertySource(String name, KNConfigService source) {
        super(name, source);
    }

    @Override
    public String[] getPropertyNames() {
        return source.getPropertyNames();
    }

    @Override
    public Object getProperty(String name) {
        return source.getProperty(name);
    }
}
