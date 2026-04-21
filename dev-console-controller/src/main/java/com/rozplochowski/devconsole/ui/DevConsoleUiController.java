package com.rozplochowski.devconsole.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DevConsoleUiController {

    @GetMapping("/dev-console")
    public String devConsoleRoot() {
        return "forward:/dev-console/index.html";
    }

    @GetMapping("/dev-console/{path:^(?!.*\\.).*$}")
    public String devConsoleRoute() {
        return "forward:/dev-console/index.html";
    }
}
