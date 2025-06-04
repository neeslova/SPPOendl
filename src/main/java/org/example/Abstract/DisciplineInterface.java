package org.example.Abstract;

import org.example.Discipline.Project;

import java.util.Iterator;
import java.util.Map;

public interface DisciplineInterface extends Iterable<String> {
    String getNames();

    Map<String, Project> getData();

    String getNameNumber(int number);

    int getQuantity();

    String saveDataToXML();

    void add(String name);

    ProjectInterface createProject(String discipline, String name);

    boolean contains(String disciplineWithRoot);

    void setDisciplineInterface(String disciplineName);

    void remove(String disciplineName);

    Iterator<String> iterator();

    void setPath(String path);

    String getPath();

    void clear();
}
