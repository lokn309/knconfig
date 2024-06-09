package cn.lokn.knconfig.client.repository;

import cn.lokn.knconfig.client.config.ConfigMeta;

import java.util.Map;

/**
 * repository change listener.
 */
public interface KNRepositoryChangeListener {

    void onChange(ChangeEvent event);

    record ChangeEvent(ConfigMeta meta, Map<String, String> config) {}

}
