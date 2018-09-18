package com.grupokinexo.tasksservice.parsers;

import com.grupokinexo.tasksservice.exceptions.ParserException;

public interface Parser {
    <T> T parseToObject(String toParse, Class<T> type) throws ParserException;

    <T> String parseToString(T toParse) throws ParserException;
}