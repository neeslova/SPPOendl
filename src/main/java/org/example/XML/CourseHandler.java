package org.example.XML;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class CourseHandler {

    private static final String XML_FILE_PATH = "resources\\data.xml";
    private static final Logger logger = Logger.getLogger(CourseHandler.class.getName());
    static {
        logger.setLevel(Level.OFF);
    }


    public static List<String> getDisciplinesByCourseNumber(int courseNumber) {
        try {
            List<Course> courses = readCoursesFromXml();
            if (courseNumber <= 0 || courseNumber > courses.size()) {
                logger.info("Номер курса находится вне зоны досягаемости: {}"+ courseNumber);
                return null;
            }
            Course course = courses.get(courseNumber - 1);
            logger.info("Получены дисциплины для курса номер {}"+ courseNumber);
            return course.getDisciplines();
        } catch (Exception e) {
            logger.severe("Ошибка при получении дисциплин для курса номер {}"+ courseNumber + e.getMessage());
            return null;
        }
    }

    public static void removeCourseByIndex(int indexToRemove) {
        RemoveEmptyLinesFromXML();
        try {
            logger.info("Начинаем удаление курса с индексом " + indexToRemove);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(XML_FILE_PATH));
            Element root = document.getDocumentElement();
            NodeList courseNodes = document.getElementsByTagName("course");

            if (indexToRemove >= 0 && indexToRemove < courseNodes.getLength()) {
                logger.info("Индекс в допустимом диапазоне");
                Element courseElement = (Element) courseNodes.item(indexToRemove);
                root.removeChild(courseElement);
                saveXmlFile(document);
                logger.info("Курс успешно удален");
                System.out.println("Курс удален");
            } else {
                logger.warning("Неверный индекс");
                System.out.println("Неверный индекс");
            }
        } catch (Exception e) {
            logger.severe("Ошибка при удалении курса" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void printCoursesName() {
        List<Course> courses = readCoursesFromXml();

        if (courses.isEmpty()) {
            logger.info("Курсы не найдены");
            System.out.println("Курсы не найдены");
            return;
        }

        logger.info("Найдено " + courses.size() + " курсов");
        int j = 1;
        for (Course course : courses) {
            logger.info(j + "-" + "\t" + course.getDirection());
            System.out.println(j + "-" + "\t" + course.getDirection());
            j++;
        }
    }

    public static void printCourses() {
        List<Course> courses = readCoursesFromXml();

        if (courses.isEmpty()) {
            logger.info("Курсы не найдены");
            System.out.println("Курсы не найдены");
            return;
        }

        logger.info("Найдено " + courses.size() + " курсов");
        int j = 1;
        for (Course course : courses) {
            logger.info(j + "-" + "\t" + course.getDirection());
            System.out.println(j + "-" + "\t" + course.getDirection());
            int i = 1;
            for (String discipline : course.getDisciplines()) {
                logger.info("\t\t" + i + "- " + discipline);
                System.out.println("\t\t" + i + "- " + discipline);
                i++;
            }
            j++;
            System.out.println();
        }

    }

    public static void addCourse(String direction, String[] disciplines) {
        RemoveEmptyLinesFromXML();
        try {
            logger.info("Начинаем добавление нового курса с направлением: " + direction);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(XML_FILE_PATH));
            Element root = document.getDocumentElement();
            Element courseElement = document.createElement("course");
            Element directionElement = document.createElement("direction");
            directionElement.setTextContent(direction);
            courseElement.appendChild(directionElement);
            Element disciplinesElement = document.createElement("disciplines");
            for (String discipline : disciplines) {
                logger.info("Добавляем дисциплину: " + discipline);
                Element disciplineElement = document.createElement("discipline");
                disciplineElement.setTextContent(discipline);
                disciplinesElement.appendChild(disciplineElement);
            }
            courseElement.appendChild(disciplinesElement);
            root.appendChild(courseElement);
            saveXmlFile(document);
            logger.info("Курс успешно добавлен");
            System.out.println("Курс добавлен");
        } catch (Exception e) {
            logger.severe("Ошибка при добавлении курса: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<Course> readCoursesFromXml() {
        List<Course> courses = new ArrayList<>();
        try {
            File file = new File(XML_FILE_PATH);
            if (!file.exists()) {
                logger.info("XML файл не существует, создаем новый");
                createXmlFile();
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            NodeList courseNodes = document.getElementsByTagName("course");
            logger.info("Найдено " + courseNodes.getLength() + " курсов");
            for (int i = 0; i < courseNodes.getLength(); i++) {
                Element courseElement = (Element) courseNodes.item(i);
                String direction = courseElement.getElementsByTagName("direction").item(0).getTextContent();
                logger.info("Обрабатываем курс с направлением: " + direction);
                NodeList disciplineNodes = courseElement.getElementsByTagName("discipline");
                List<String> disciplines = new ArrayList<>();
                for (int j = 0; j < disciplineNodes.getLength(); j++) {
                    String discipline = disciplineNodes.item(j).getTextContent();
                    logger.fine("Добавляем дисциплину: " + discipline);
                    disciplines.add(discipline);
                }
                Course course = new Course(direction, disciplines);
                courses.add(course);
            }
        } catch (Exception e) {
            logger.severe("Ошибка при чтении XML файла: " + e.getMessage());
            e.printStackTrace();
        }
        return courses;
    }

    private static void createXmlFile() {

        try {
            logger.info("Создание нового XML файла");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            Element rootElement = document.createElement("courses");
            document.appendChild(rootElement);
            saveXmlFile(document);
            logger.info("XML файл успешно создан");
        } catch (Exception e) {
            logger.severe("Ошибка при создании XML файла: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void saveXmlFile(Document document) {

        try {
            logger.info("Сохранение XML файла");
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();


            // Добавляем параметры форматирования
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");



            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(XML_FILE_PATH));
            transformer.transform(source, result);
            logger.info("XML файл успешно сохранен");
        } catch (Exception e) {
            logger.severe("Ошибка при сохранении XML файла: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void RemoveEmptyLinesFromXML(){
        // Чтение из файла и удаление пустых строк
        try (BufferedReader reader = new BufferedReader(new FileReader(XML_FILE_PATH))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Проверка, является ли строка пустой или содержит только пробельные символы
                if (!line.trim().isEmpty()) {
                    stringBuilder.append(line).append("\n");
                }
            }

            // Запись обновленного содержимого обратно в файл
            try (FileWriter writer = new FileWriter(XML_FILE_PATH)) {
                writer.write(stringBuilder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
