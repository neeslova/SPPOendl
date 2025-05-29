package org.example.Discipline;

import org.example.Abstract.ProjectInterface;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Project implements ProjectInterface {

    private final Map<String, Task> name = new HashMap<>();

    public Map<String, Task> getData() {
        return name;
    }

    public String getName() {
        Set<String> setKeys = this.name.keySet();
        StringBuilder a = new StringBuilder("Дисциплины: \n");
        for (String k : setKeys) {
            a.append(k).append("\n");
        }
        if (a.toString().equals("Дисциплины: \n")) {
            return null;
        }
        return a.toString();
    }

    public void add(String name) {
        this.name.put(name, new Task());
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        try {
            for (Map.Entry<String, Task> entry : name.entrySet()) {
                result.append("\n\t\t\t").append(entry.getKey()).append(": ").append(entry.getValue());
            }
            return result.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public void createTask(String project, String description, String type) {
        Task task = this.name.get(project);
        task.add(description, type);
        this.name.put(project, task);
    }
}
