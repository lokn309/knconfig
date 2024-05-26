package cn.lokn.knconfig.client.annotation;

import cn.lokn.knconfig.client.config.KNConfigRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * KnConfig client entrypoint.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import({KNConfigRegistrar.class})
public @interface EnableKnConfig {
}
