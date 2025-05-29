package org.example.Abstract;

import java.util.List;

public interface TaskInterface {
    void add(String description, String type);

    List<String[]> getData();

    String getDescription();
}
