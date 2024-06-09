package cn.lokn.knconfig.client.repository;

import cn.lokn.knconfig.client.config.ConfigMeta;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * interface to get config form remote.
 */
public interface KNRepository {

    static KNRepository getDefault(ApplicationContext applicationContext, ConfigMeta configMeta) {
        return new KNRepositoryImpl(applicationContext, configMeta);
    }

    Map<String, String> getConfig();

    void addListener(KNRepositoryChangeListener listener);


}
