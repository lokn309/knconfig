package cn.lokn.knconfig.client.config;

import cn.lokn.knconfig.client.repository.KNRepository;

/**
 * @description:
 * @author: lokn
 * @date: 2024/05/19 22:53
 */
public interface KNConfigService {

    static KNConfigService getDefault(ConfigMeta configMeta) {
        KNRepository repository = KNRepository.getDefault(configMeta);
        return new KNConfigServiceImpl(repository.getConfig());
    }

    String[] getPropertyNames();

    String getProperty(String name);

}
