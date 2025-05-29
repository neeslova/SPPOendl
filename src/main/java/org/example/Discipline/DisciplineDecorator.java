package org.example.Discipline;

import org.example.Abstract.DisciplineInterface;
import org.example.Abstract.ProjectInterface;

import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class DisciplineDecorator implements DisciplineInterface {
    private final DisciplineInterface decoratedDiscipline1;
    private final DisciplineInterface decoratedDiscipline2;


    public DisciplineDecorator(DisciplineInterface decoratedDiscipline1, DisciplineInterface decoratedDiscipline2) {
        this.decoratedDiscipline1 = decoratedDiscipline1;
        this.decoratedDiscipline2 = decoratedDiscipline2;

    }

    @Override
    public String saveDataToXML() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите имя пользователя: ");
        String name = scanner.nextLine();
        setPath(name + ".xml");
        decoratedDiscipline1.saveDataToXML();
        decoratedDiscipline2.saveDataToXML();

        System.out.println("Данные сохранены");
        return null;
    }

    // Остальные методы проксируются без изменений
    @Override
    public Map<String, Project> getData() {
        return decoratedDiscipline1.getData();
    }

    @Override
    public String getNames() {
        return decoratedDiscipline1.getNames();
    }

    @Override
    public String getNameNumber(int number) {
        return decoratedDiscipline1.getNameNumber(number);
    }

    @Override
    public int getQuantity() {
        return decoratedDiscipline1.getQuantity();
    }

    @Override
    public void add(String name) {
        decoratedDiscipline1.add(name);
    }

    @Override
    public ProjectInterface createProject(String discipline, String name) {
        return decoratedDiscipline1.createProject(discipline, name);
    }

    @Override
    public boolean contains(String name) {
        return decoratedDiscipline1.contains(name);
    }

    @Override
    public void setDiscipline(String disciplineName) {
        decoratedDiscipline1.setDiscipline(disciplineName);
    }

    @Override
    public void remove(String disciplineName) {
        decoratedDiscipline1.remove(disciplineName);
    }

    @Override
    public Iterator<String> iterator() {
        return decoratedDiscipline1.iterator();
    }

    @Override
    public void setPath(String path) {
        decoratedDiscipline1.setPath(path);
        decoratedDiscipline2.setPath(path);
    }

    @Override
    public String getPath() {
        return decoratedDiscipline1.getPath();
    }

    @Override
    public void clear() {
        decoratedDiscipline1.clear();
    }
}

