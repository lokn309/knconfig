package cn.lokn.knconfig.client.repository;

import cn.kimmking.utils.HttpUtils;
import cn.lokn.knconfig.client.config.ConfigMeta;
import com.alibaba.fastjson.TypeReference;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @description: default impl for kn repository.
 * @author: lokn
 * @date: 2024/05/27 23:48
 */
@AllArgsConstructor
public class KNRepositoryImpl implements KNRepository {

    ConfigMeta meta;
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    Map<String, Long> versionMap = new HashMap<>();
    Map<String, Map<String, String>> configMap = new HashMap<>();
    List<KNRepositoryChangeListener> listeners = new ArrayList<>();

    public KNRepositoryImpl(ApplicationContext applicationContext, ConfigMeta configMeta) {
        this.meta = configMeta;
        executor.scheduleWithFixedDelay(this::heartbeat, 1, 5, TimeUnit.SECONDS);
    }

    public void addListener(KNRepositoryChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public Map<String, String> getConfig() {
        String key = meta.genKey();
        if (configMap.containsKey(key)) {
            return configMap.get(key);
        }
        return findAll();
    }

    @NotNull
    private Map<String, String> findAll() {
        String listPath = meta.listPath();
        System.out.println("[KNConfig] list all configs from kn config server.");
        List<Configs> configs = HttpUtils.httpGet(listPath, new TypeReference<List<Configs>>() {
        });
        Map<String, String> resultMap = new HashMap<>();
        configs.forEach(c -> resultMap.put(c.getPkey(), c.getPval()));
        return resultMap;
    }


    private void heartbeat() {
        String versionPath = meta.versionPath();
        Long version = HttpUtils.httpGet(versionPath, new TypeReference<Long>() {
        });
        String key = meta.genKey();
        Long oldVersion = versionMap.getOrDefault(key, -1L);
        if (version > oldVersion) {
            System.out.println("[KNCONFIG] current = " + version + ", old = " + oldVersion);
            System.out.println("[KNCONFIG] need update new configs. ");
            versionMap.put(key, version);
            final Map<String, String> newConfigs = findAll();
            configMap.put(key, newConfigs);
            // 保证先修改数据的源，再通知事件
            listeners.forEach(l -> l.onChange(new KNRepositoryChangeListener.ChangeEvent(meta, newConfigs)));
        }

    }

}
