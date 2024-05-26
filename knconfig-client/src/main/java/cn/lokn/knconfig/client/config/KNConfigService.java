package cn.lokn.knconfig.client.config;

/**
 * @description:
 * @author: lokn
 * @date: 2024/05/19 22:53
 */
public interface KNConfigService {

    String[] getPropertyNames();

    String getProperty(String name);

}
