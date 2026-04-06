package com.rozplochowski.devconsole.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DevConsoleUiController {

    @GetMapping("/dev-console")
    public String devConsoleRoot() {
        return "forward:/dev-console/index.html";
    }

    @GetMapping("/dev-console/{resource}")
    public String devConsoleResource(@PathVariable String resource) {
        return "forward:/dev-console/%s".formatted(resource);
    }
}
