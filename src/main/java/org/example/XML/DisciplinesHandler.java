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


public class DisciplinesHandler {

    private static final String XML_FILE_PATH = "resources\\disciplines.xml";
    private static final Logger logger = Logger.getLogger(DisciplinesHandler.class.getName());
    static {
        logger.setLevel(Level.OFF);
    }


    public static String getDisciplineByNumber(int number) {
        List<String> disciplines = readDisciplines();

        if (number < 0 || number >= disciplines.size()+1) {
            return null;
        }

        return disciplines.get(number-1);
    }

    public static List<String> readDisciplines() {

        List<String> disciplines = new ArrayList<>();
        try {
            logger.info("Начинаем чтение дисциплин из XML файла");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(XML_FILE_PATH));
            NodeList disciplineNodes = document.getElementsByTagName("discipline");
            logger.info("Найдено " + disciplineNodes.getLength() + " дисциплин");
            for (int i = 0; i < disciplineNodes.getLength(); i++) {
                Element disciplineElement = (Element) disciplineNodes.item(i);
                String discipline = disciplineElement.getTextContent();
                logger.fine("Добавляем дисциплину: " + discipline);
                disciplines.add(discipline);
            }
            logger.info("Чтение дисциплин завершено");
        } catch (Exception e) {
            logger.severe("Ошибка при чтении дисциплин из XML файла: " + e.getMessage());
            e.printStackTrace();
        }
        return disciplines;
    }

    public static void addDiscipline(String disciplineName) {
        RemoveEmptyLinesFromXML();
        try {
            logger.info("Начинаем добавление дисциплины: " + disciplineName);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document;
            File file = new File(XML_FILE_PATH);
            if (!file.exists()) {
                logger.info("XML файл не существует, создаем новый");
                document = builder.newDocument();
                Element rootElement = document.createElement("disciplines");
                document.appendChild(rootElement);
            } else {
                logger.info("XML файл существует, читаем содержимое");
                document = builder.parse(file);
            }
            Element root = document.getDocumentElement();
            Element disciplineElement = document.createElement("discipline");
            disciplineElement.setTextContent(disciplineName);
            root.appendChild(disciplineElement);
            saveXmlFile(document);
            logger.info("Дисциплина успешно добавлена");
        } catch (Exception e) {
            logger.severe("Ошибка при добавлении дисциплины: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void removeDiscipline(int disciplineIndex) {
        RemoveEmptyLinesFromXML();
        try {
            logger.info("Начинаем удаление дисциплины с индексом: " + disciplineIndex);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(XML_FILE_PATH));
            Element root = document.getDocumentElement();
            NodeList disciplineNodes = document.getElementsByTagName("discipline");
            int disciplineCount = disciplineNodes.getLength();
            logger.info("Найдено " + disciplineCount + " дисциплин");
            if (disciplineIndex >= 0 && disciplineIndex < disciplineCount) {
                Element disciplineElement = (Element) disciplineNodes.item(disciplineIndex);
                root.removeChild(disciplineElement);
                saveXmlFile(document);
                logger.info("Дисциплина успешно удалена");
            } else {
                logger.warning("Индекс дисциплины выходит за пределы списка");
            }
        } catch (Exception e) {
            logger.severe("Ошибка при удалении дисциплины: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void printDisciplines() {

        logger.info("Начинаем вывод списка дисциплин");
        List<String> disciplines = readDisciplines();
        if (disciplines.isEmpty()) {
            logger.warning("Дисциплины не найдены");
            return;
        }
        logger.info("Список дисциплин:");
        int i = 1;
        for (String discipline : disciplines) {
            logger.info(i + " - " + discipline);
            System.out.println(i + " - " + discipline);
            i++;
        }
    }

    private static void saveXmlFile(Document document) {

        try {
            logger.info("Начинаем сохранение XML файла");
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