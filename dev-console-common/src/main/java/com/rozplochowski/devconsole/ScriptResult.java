package com.rozplochowski.devconsole;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ScriptResult.Success.class),
        @JsonSubTypes.Type(value = ScriptResult.Error.class)
})
public sealed interface ScriptResult {

    record Success(String result, String stdOut) implements ScriptResult {}
    record Error(String error, String stackTrace) implements ScriptResult {}
}
