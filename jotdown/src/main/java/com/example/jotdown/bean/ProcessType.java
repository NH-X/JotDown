package com.example.jotdown.bean;

public enum ProcessType {
    query_executing,
    query_failing,
    query_successful,

    delete_executing,
    delete_failing,
    delete_successful,

    update_executing,
    update_successful,
    update_failing,

    insert_executing,
    insert_successful,
    insert_failing
}
