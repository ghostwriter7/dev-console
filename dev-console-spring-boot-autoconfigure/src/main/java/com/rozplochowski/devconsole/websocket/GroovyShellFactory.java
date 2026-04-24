package com.rozplochowski.devconsole.websocket;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class GroovyShellFactory {

    static GroovyShell fromBinding(Binding binding) {
        var importCustomizer = new ImportCustomizer();
        importCustomizer.addImports("groovy.json.JsonOutput");
        importCustomizer.addStarImports("java.util", "java.time");

        var compilerConfig = new CompilerConfiguration();
        compilerConfig.addCompilationCustomizers(importCustomizer);

        return new GroovyShell(GroovyShellFactory.class.getClassLoader(), binding, compilerConfig);
    }
}
