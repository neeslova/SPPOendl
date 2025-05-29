package org.example.Abstract;

import org.example.Discipline.Task;

import java.util.Map;

public interface ProjectInterface {
    String getName();

    Map<String, Task> getData();

    void add(String name);

    void createTask(String project, String description, String type);
}
