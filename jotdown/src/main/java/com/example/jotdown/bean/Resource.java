package com.example.jotdown.bean;

public class Resource<T> {
    private final T data;
    private final QueryProcessType type;
    private final String message;

    public Resource(T data,QueryProcessType type, String message) {
        this.data = data;
        this.type=type;
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public QueryProcessType getType(){
        return type;
    }

    public String getMessage() {
        return message;
    }
}