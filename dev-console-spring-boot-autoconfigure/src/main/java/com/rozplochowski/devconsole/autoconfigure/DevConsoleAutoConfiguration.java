package com.rozplochowski.devconsole.autoconfigure;

import com.rozplochowski.devconsole.websocket.DevConsoleWebSocketHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(DevConsoleProperties.class)
@ConditionalOnProperty(prefix = "dev.console", name = "enabled", havingValue = "true")
public class DevConsoleAutoConfiguration {

    @Bean
    public DevConsoleWebSocketHandler devConsoleWebSocketHandler(
            ApplicationContext applicationContext,
            Environment environment,
            DevConsoleProperties properties
    ) {
        return new DevConsoleWebSocketHandler(applicationContext, environment, properties);
    }

    @Bean
    public DevConsoleWebSocketConfigurer devConsoleWebSocketConfigurer(
            DevConsoleWebSocketHandler handler,
            DevConsoleProperties properties
    ) {
        return new DevConsoleWebSocketConfigurer(handler, properties);
    }
}
