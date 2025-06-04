package org.example.Discipline;

import org.example.Abstract.DisciplineInterface;
import org.example.Abstract.ProjectInterface;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OptionalDiscipline implements DisciplineInterface {
    private static final Logger logger = Logger.getLogger(OptionalDiscipline.class.getName());



    private final Map<String, Project> names = new HashMap<>();
    private int quantity;
    private String path = "user1.xml";

    public boolean contains(String name) {
        return this.names.containsKey(name);
    }

    public OptionalDiscipline() {
        this.quantity = 1;
    }

    public void add(String name) {

        int requiredCount = 5;
        if (this.quantity < requiredCount) {
            this.names.put(name, new Project());
            this.quantity += 1;
        }
    }

    public Map<String, Project> getData() {
        logger.fine("Получение данных дополнительных дисциплин");
        return names;
    }

    @Override
    public String getNames() {
        try {
            Set<String> setKeys = this.names.keySet();
            StringBuilder a = new StringBuilder("\nДополнительные дисциплины: \n\t");
            int i = 1;
            for (String k : setKeys) {
                a.append("\n").append(i).append(". ").append(k);
                i += 1;
            }
            return a.toString();
        } catch (Exception e) {
            logger.severe("Ошибка при получении названий обязательных дисциплин: " + e.getMessage());
            return "Ошибка при получении названий обязательных дисциплин";
        }
    }

    @Override
    public String getNameNumber(int number) {
        try {
            logger.fine("Получение названия дополнительной дисциплины по номеру: " + number);
            Set<String> setKeys = this.names.keySet();
            StringBuilder a = new StringBuilder("Дополнительные дисциплины: \n\t");
            int i = 1;
            for (String k : setKeys) {
                if (number == i) {
                    return k;
                }
                a.append(i).append(". ").append(k).append("\n\t");
                i += 1;
            }
            return a.toString();
        } catch (Exception e) {
            logger.severe("Ошибка при получении названия обязательной дисциплины по номеру: " + e.getMessage());
            return "Ошибка при получении названия обязательной дисциплины по номеру";
        }
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String saveDataToXML() {
        StringBuilder result = new StringBuilder("\nДополнительные дисциплины:");
        int i = 1;
        for (Map.Entry<String, Project> entry : names.entrySet()) {
            result.append("\n").append("\t").append(i).append(". ").append(entry.getKey());
            i++;
            Project project = entry.getValue();

            if (Objects.equals(project.getName(), null)) {
                result.append(project);
            } else {
                result.append("\n\t\tПроекты:").append(project);
            }
        }
        return result.toString();
    }

    public ProjectInterface createProject(String discipline, String name) {
        logger.info("Создание проекта: " + discipline + ", " + name);
        try {
            Project project = this.names.get(discipline);
            project.add(name);
            return this.names.put(discipline, project);
        } catch (Exception e) {
            logger.severe("Ошибка при создании проекта" + e.getMessage());
            return null;
        }
    }

    public void setDisciplineInterface(String disciplineName) {
        try {
            logger.info("Добавление дополнительной дисциплины: " + disciplineName);
            this.names.put(disciplineName, new Project());
        } catch (Exception e) {
            logger.severe("Ошибка при добавлении дополнительной дисциплины: " + e.getMessage());
        }
    }

    public void remove(String disciplineName) {
        try {
            logger.info("Удаление дополниетльной дисциплины: " + disciplineName);
            if (this.names.containsKey(disciplineName)) {
                this.names.remove(disciplineName);
                this.quantity -= 1;
            }
        } catch (Exception e) {
            logger.severe("Ошибка при удалении дисиплины" + e.getMessage());
        }
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<>() {
            private final Iterator<Map.Entry<String, Project>> iterator = names.entrySet().iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public String next() {
                return iterator.next().getKey();
            }

            @Override
            public void remove() {
                iterator.remove();
                quantity--;
            }
        };
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void clear() {
        names.clear();
    }
}
