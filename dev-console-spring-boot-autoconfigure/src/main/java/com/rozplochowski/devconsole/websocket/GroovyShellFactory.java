package com.rozplochowski.devconsole.websocket;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

class GroovyShellFactory {

    GroovyShell fromBinding(Binding binding) {
        var importCustomizer = new ImportCustomizer();
        importCustomizer.addImports("groovy.json.JsonOutput");
        importCustomizer.addStarImports("java.util", "java.time");

        var compilerConfig = new CompilerConfiguration();
        compilerConfig.addCompilationCustomizers(importCustomizer);

        return new GroovyShell(getClass().getClassLoader(), binding, compilerConfig);
    }
}
