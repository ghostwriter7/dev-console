package com.rozplochowski.devconsole.autoconfigure;

import com.rozplochowski.devconsole.websocket.DevConsoleWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

@Slf4j
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(DevConsoleProperties.class)
@ConditionalOnProperty(prefix = "dev.console", name = "enabled", havingValue = "true")
@Import(DevConsoleWebSocketConfigurer.class)
public class DevConsoleAutoConfiguration {

    @Bean
    public DevConsoleWebSocketHandler devConsoleWebSocketHandler(ApplicationContext context, Environment env, DevConsoleProperties properties) {
        return new DevConsoleWebSocketHandler(context, env, properties);
    }

}
