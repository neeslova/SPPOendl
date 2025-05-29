package org.example.Discipline;

import org.example.Abstract.TaskInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Task implements TaskInterface {

    private final List<Object[]> data = new ArrayList<>();
    private static final Logger logger = Logger.getLogger(Task.class.getName());



    public List<String[]> getData() {
        List<String[]> resultList = new ArrayList<>();
        try {
            for (Object[] pair : data) {
                resultList.add(new String[]{pair[0].toString(), pair[1].toString()});
            }
        } catch (Exception e) {
            logger.severe("Ошибка при получении данных: " + e.getMessage());
        }
        return resultList;
    }

    @Override
    public String getDescription() {
        try {
            for (Object[] pair : data) {
                return (String) pair[1];
            }
        } catch (Exception e) {
            logger.severe("Ошибка при получении описания: " + e.getMessage());
        }
        return null;
    }

    public void add(String description, String type) {
        try {
            Object[] pair = new Object[2];
            pair[0] = type;
            pair[1] = description;
            this.data.add(pair);
        } catch (Exception e) {
            logger.severe("Ошибка при добавлении данных: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        try {
            for (Object[] pair : data) {
                sb.append("\n\t\t\t\t[Тип: ").append(pair[0]).append(", Описание: ").append(pair[1]).append("],");
            }
        } catch (Exception e) {
            logger.severe("Ошибка при преобразовании в строку: " + e.getMessage());
        }
        return sb.toString();
    }
}

