package com.rozplochowski.devconsole.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "dev.console")
public class DevConsoleProperties {
    private boolean enabled = false;
    private List<String> allowedOrigins = List.of("*", "http://localhost:4200");
    private String path = "/v1/dev/console/ws";
}
