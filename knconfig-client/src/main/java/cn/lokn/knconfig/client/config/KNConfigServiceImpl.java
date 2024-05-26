package cn.lokn.knconfig.client.config;

import java.util.Map;

/**
 * @description:
 * @author: lokn
 * @date: 2024/05/19 23:40
 */
public class KNConfigServiceImpl implements KNConfigService {

    Map<String, String> config;

    public KNConfigServiceImpl(Map<String, String> config) {
        this.config = config;
    }

    @Override
    public String[] getPropertyNames() {
        return this.config.keySet().toArray(new String[0]);
    }

    @Override
    public String getProperty(String name) {
        return this.config.get(name);
    }
}
