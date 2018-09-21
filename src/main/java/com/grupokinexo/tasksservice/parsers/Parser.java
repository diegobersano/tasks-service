package com.grupokinexo.tasksservice.parsers;

import com.grupokinexo.tasksservice.exceptions.ParserException;

import java.util.Collection;

public interface Parser {
    <T> T parseToObject(String toParse, Class<T> type) throws ParserException;

    <T> Collection<T> parseToListObject(String toParse, Class<T> type) throws ParserException;

    <T> String parseToString(T toParse) throws ParserException;
}