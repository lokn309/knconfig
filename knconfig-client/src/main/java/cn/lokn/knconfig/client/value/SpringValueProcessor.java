package cn.lokn.knconfig.client.value;

import cn.kimmking.utils.FieldUtils;
import cn.lokn.knconfig.client.util.PlaceholderHelper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Field;
import java.util.List;

/**
 * process spring value.
 * 1.扫描所有的 spring value，保存起来
 * 2.在配置变更时，更新所有的spring value
 *
 * @author: lokn
 * @date: 2024/06/02 23:44
 */
@Slf4j
public class SpringValueProcessor implements BeanPostProcessor, BeanFactoryAware, ApplicationListener<EnvironmentChangeEvent> {

    static final PlaceholderHelper helper = PlaceholderHelper.getInstance();
    static final MultiValueMap<String, SpringValue> VALUE_HOLDER = new LinkedMultiValueMap<>();

    @Setter
    private BeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 第一步
        final List<Field> fields = FieldUtils.findAnnotatedField(bean.getClass(), Value.class);
        fields.forEach(field -> {
            log.info("[KNCONFIG] >> find spring value: {}", field);
            Value value = field.getAnnotation(Value.class);
            helper.extractPlaceholderKeys(value.value()).forEach(
                    key -> {
                        log.info("[KNCONFIG] >> find spring value: {}", key);
                        final SpringValue springValue = new SpringValue(bean, beanName, key, value.value(), field);
                        VALUE_HOLDER.add(key, springValue);
                    }
            );
        });
        return bean;
    }

    // 该方法与 ApplicationListener 的 onChange 方法作用相同，目前采用下面的方式来现实
//    @EventListener
//    public void onChange(EnvironmentChangeEvent event) {
//
//    }

    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        log.info("[KNCONFIG] >> update spring value for keys: {}", event.getKeys());
        // 第二步
        event.getKeys().forEach(key -> {
            log.info("[KNCONFIG] >> update spring value: {}", key);
            List<SpringValue> springValues = VALUE_HOLDER.get(key);
            if (springValues == null || springValues.isEmpty()) {
                return;
            }
            springValues.forEach(springValue -> {
                log.info("[KNCONFIG] >> update spring value: {} for key {}", springValue, key);
                try {
                    Object value = helper.resolvePropertyValue((ConfigurableBeanFactory) beanFactory,
                            springValue.getBeanName(), springValue.getPlaceholder());
                    log.info("[KNCONFIG] >> update value: {} for holder {}", value, springValue.getPlaceholder());
                    springValue.getField().setAccessible(true);
                    springValue.getField().set(springValue.getBean(), value);
                } catch (Exception e) {
                    log.error("[KNCONFIG] >> update spring value error", e);
                }
            });
        });
    }
}
