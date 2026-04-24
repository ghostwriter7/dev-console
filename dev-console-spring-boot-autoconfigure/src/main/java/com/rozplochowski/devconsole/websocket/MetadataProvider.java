package com.rozplochowski.devconsole.websocket;

import com.rozplochowski.devconsole.Symbol;
import com.rozplochowski.devconsole.TypeMetadata;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class MetadataProvider {
    private final ApplicationContext context;

    MetadataBundle provide(Set<String> excludedNames) {
        List<TypeMetadata> typeMetadataList = new ArrayList<>();
        List<Symbol> symbols = new ArrayList<>();
        List<SpringNameAndBeanPair> beanPairs = new ArrayList<>();
        for (var name : context.getBeanDefinitionNames()) {
            if (!excludedNames.contains(name)) {
                var bean = context.getBean(name);
                Class<?> beanClazz = bean.getClass();
                typeMetadataList.add(TypeMetadataFactory.from(beanClazz));
                var variableName = name.substring(0, 1).toLowerCase() + name.substring(1);
                symbols.add(new Symbol(variableName, TypeMetadataFactory.toTypeInfo(beanClazz)));
                beanPairs.add(new SpringNameAndBeanPair(name, bean));
            }
        }

        return new MetadataBundle(typeMetadataList, symbols, beanPairs);
    }

    public record MetadataBundle(List<TypeMetadata> typeMetadataList,
                                 List<Symbol> symbols,
                                 List<SpringNameAndBeanPair> beanPairs
                                 ) {
    }

    public record SpringNameAndBeanPair(String name, Object bean) {}
}
