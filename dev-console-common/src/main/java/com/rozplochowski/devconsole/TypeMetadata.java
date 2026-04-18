package com.rozplochowski.devconsole;

import java.util.List;


public record TypeMetadata(String type, String fullName, List<MethodMetadata> methods) {
    public record TypeInfo(String type, String fullName) {}

    public record MethodMetadata(String name, TypeInfo returnType, List<TypeInfo> parameters) {}
}
