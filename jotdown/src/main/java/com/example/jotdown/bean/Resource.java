package com.example.jotdown.bean;

public class Resource<T> {
    private final T data;
    private final ProcessType type;
    private final String message;

    public Resource(T data, ProcessType type, String message) {
        this.data = data;
        this.type=type;
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public ProcessType getType(){
        return type;
    }

    public String getMessage() {
        return message;
    }
}