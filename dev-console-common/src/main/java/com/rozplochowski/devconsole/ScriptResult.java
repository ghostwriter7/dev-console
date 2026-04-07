package com.rozplochowski.devconsole;

public sealed interface ScriptResult {

    record Success(String result, String stdOut) implements ScriptResult {}
    record Error(String error, String stackTrace) implements ScriptResult {}
}
