package com.rozplochowski.devconsole.autoconfigure;

import com.rozplochowski.devconsole.websocket.DevConsoleConnector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Slf4j
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(DevConsoleProperties.class)
@ConditionalOnProperty(prefix = "dev.console", name = "enabled", havingValue = "true")
@Import(DevConsoleWebSocketConfigurer.class)
@ComponentScan(basePackageClasses = DevConsoleConnector.class)
public class DevConsoleAutoConfiguration {
}
