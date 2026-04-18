package com.rozplochowski.devconsole;

import java.util.List;


public record TypeMetadata(String type, String fullName, List<MethodMetadata> methods) {
    public record MethodMetadata(String name, String returnType, List<String> parameters) {}
}
