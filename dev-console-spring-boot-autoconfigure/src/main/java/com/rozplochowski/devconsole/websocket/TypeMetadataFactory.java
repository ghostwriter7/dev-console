package com.rozplochowski.devconsole.websocket;

import com.rozplochowski.devconsole.TypeMetadata;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class TypeMetadataFactory {

    static TypeMetadata from(Object bean) {
        var clazz = bean.getClass();

        var publicMethods = Arrays.stream(bean.getClass().getMethods())
                .filter(m -> !m.isBridge() && !m.isSynthetic())
                .map(TypeMetadataFactory::toMethodMetadata)
                .toList();

        return new TypeMetadata(clazz.getSimpleName(), clazz.getName(), publicMethods);
    }

    private static TypeMetadata.MethodMetadata toMethodMetadata(Method method) {
        return new TypeMetadata.MethodMetadata(
                method.getName(),
                method.getReturnType().getSimpleName(),
                Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).toList()
        );
    }

}
