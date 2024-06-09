package cn.lokn.knconfig.client.config;

import cn.lokn.knconfig.client.repository.KNRepository;
import cn.lokn.knconfig.client.repository.KNRepositoryChangeListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: lokn
 * @date: 2024/05/19 23:40
 */
@Slf4j
public class KNConfigServiceImpl implements KNConfigService, KNRepositoryChangeListener {

    Map<String, String> config;
    ApplicationContext applicationContext;

    public KNConfigServiceImpl(ApplicationContext applicationContext, Map<String, String> config) {
        this.applicationContext = applicationContext;
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

    @Override
    public void onChange(KNRepositoryChangeListener.ChangeEvent event) {
        // 只变更变化了的key
        Set<String> keys = calcChangedKeys(this.config, event.config());
        if (keys.isEmpty()) {
            log.info("[KNCONFIG] calcChangedKeys return empty, ignore update.");
            return;
        }
        this.config = event.config();
        if (!config.isEmpty()) {
            log.info("[KNCONFIG] fire an EnvironmentChangeEvent with keys: {}", keys);
            applicationContext.publishEvent(new EnvironmentChangeEvent(keys));
        }
    }

    private Set<String> calcChangedKeys(Map<String, String> oldConfigs, Map<String, String> newConfigs) {
        if (oldConfigs == null) return newConfigs.keySet();
        if (newConfigs == null) return oldConfigs.keySet();
        Set<String> news = newConfigs.keySet().stream()
                .filter(key -> !newConfigs.get(key).equals(oldConfigs.get(key)))
                .collect(Collectors.toSet());
        // 获取旧的，但是不在新的key
        oldConfigs.keySet().stream().filter(key -> !newConfigs.containsKey(key)).forEach(news::add);
        return news;
    }
}
