package cn.lokn.knconfig.client.config;

import cn.lokn.knconfig.client.repository.KNRepository;
import cn.lokn.knconfig.client.repository.KNRepositoryChangeListener;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * @description:
 * @author: lokn
 * @date: 2024/05/19 22:53
 */
public interface KNConfigService extends KNRepositoryChangeListener {

    static KNConfigService getDefault(ApplicationContext applicationContext, ConfigMeta configMeta) {
        KNRepository repository = KNRepository.getDefault(applicationContext, configMeta);
        Map<String, String> config = repository.getConfig();
        KNConfigService configService = new KNConfigServiceImpl(applicationContext, config);
        repository.addListener(configService);
        return configService;
    }

    String[] getPropertyNames();

    String getProperty(String name);

}
