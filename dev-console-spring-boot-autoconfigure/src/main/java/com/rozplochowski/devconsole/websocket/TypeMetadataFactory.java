package com.rozplochowski.devconsole.websocket;

import com.rozplochowski.devconsole.TypeMetadata;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TypeMetadataFactory {

    public static TypeMetadata from(Class<?> clazz) {

        var publicMethods = Arrays.stream(clazz.getMethods())
                .filter(m -> !m.isBridge() && !m.isSynthetic())
                .map(TypeMetadataFactory::toMethodMetadata)
                .toList();

        return new TypeMetadata(clazz.getSimpleName(), clazz.getName(), publicMethods);
    }

    private static TypeMetadata.MethodMetadata toMethodMetadata(Method method) {
        return new TypeMetadata.MethodMetadata(
                method.getName(),
                toTypeInfo(method.getReturnType()),
                Arrays.stream(method.getParameterTypes()).map(TypeMetadataFactory::toTypeInfo).toList()
        );
    }

    static TypeMetadata.TypeInfo toTypeInfo(Class<?> clazz) {
        return new TypeMetadata.TypeInfo(clazz.getSimpleName(), clazz.getName());
    }

}
