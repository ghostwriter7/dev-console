package com.rozplochowski.devconsole.handler;

import com.rozplochowski.devconsole.Symbol;
import com.rozplochowski.devconsole.TypeMetadata;

import java.util.List;

record ServerContext(List<TypeMetadata> typeMetadata, List<Symbol> symbols) {
}
