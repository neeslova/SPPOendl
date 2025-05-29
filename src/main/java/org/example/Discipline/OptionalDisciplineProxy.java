package org.example.Discipline;

import org.example.Abstract.DisciplineInterface;
import org.example.Abstract.ProjectInterface;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OptionalDisciplineProxy implements DisciplineInterface {
    private static final Logger logger = Logger.getLogger(OptionalDisciplineProxy.class.getName());

    private final DisciplineInterface optionalDiscipline;

    public OptionalDisciplineProxy(DisciplineInterface optionalDiscipline) {
        this.optionalDiscipline = optionalDiscipline;
    }

    public Map<String, Project> getData() {
        return optionalDiscipline.getData();
    }

    @Override
    public String getNames() {
        optionalDiscipline.getNames();
        return null;
    }

    @Override
    public String getNameNumber(int number) {
        return optionalDiscipline.getNameNumber(number);
    }

    @Override
    public int getQuantity() {
        return optionalDiscipline.getQuantity();
    }

    @Override
    public String saveDataToXML() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc;

            // Проверяем, существует ли уже файл

            // Путь к файлу "User.xml"
            String filePath = "resources\\users" + File.separator + optionalDiscipline.getPath();
            File file = new File(filePath);

            if (file.exists()) {
                doc = dBuilder.parse(file);
                Element rootElement = doc.getDocumentElement();
                Element disciplines = (Element) rootElement.getElementsByTagName("disciplines").item(0);

                Map<String, Project> name = optionalDiscipline.getData();

                for (Map.Entry<String, Project> entry : name.entrySet()) {
                    Project project = entry.getValue();
                    Map<String, Task> task = project.getData();

                    addDiscipline(doc, disciplines, entry.getKey(), task);
                }
            } else {
                doc = dBuilder.newDocument();
                // Создание корневого элемента
                Element rootElement = doc.createElement("User");
                doc.appendChild(rootElement);

                // Добавление дисциплин
                Element disciplines = doc.createElement("disciplines");
                rootElement.appendChild(disciplines);

                Map<String, Project> name = optionalDiscipline.getData();

                for (Map.Entry<String, Project> entry : name.entrySet()) {
                    Project project = entry.getValue();
                    Map<String, Task> task = project.getData();

                    addDiscipline(doc, disciplines, entry.getKey(), task);
                }
            }

            // Запись содержимого в файл
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // Добавляем форматирование
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(filePath);

            transformer.transform(source, result);

            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void addDiscipline(Document doc, Element parent, String disciplineName, Map<String, Task> projects) {

        Element discipline = doc.createElement("discipline");
        discipline.setAttribute("name", disciplineName);
        parent.appendChild(discipline);

        for (Map.Entry<String, Task> entry : projects.entrySet()) {
            String projectName = entry.getKey();  // Получение имени проекта
            Task projectTask = entry.getValue();  // Получение задачи проекта

            Element project = doc.createElement("project");
            project.setAttribute("name", projectName);
            discipline.appendChild(project);

            // Добавление задач проекта
            addTask(doc, project, projectTask.getData(), projectTask.getDescription());
        }
    }

    private static void addTask(Document doc, Element parent, List<String[]> taskNames, String description) {
        for (String[] names : taskNames) {
            Element task = doc.createElement("task");

            // Используем только первый элемент из массива имен
            String taskName = names[0];
            task.setAttribute("name", taskName);


            task.appendChild(doc.createTextNode(names[1]));
            parent.appendChild(task);
        }
    }


    @Override
    public void add(String name) {
        optionalDiscipline.add(name);
    }

    public ProjectInterface createProject(String discipline, String name) {
        return optionalDiscipline.createProject(discipline, name);
    }

    public boolean contains(String name) {
        return optionalDiscipline.contains(name);
    }

    public void setDiscipline(String disciplineName) {
        optionalDiscipline.setDiscipline(disciplineName);
    }

    public void remove(String disciplineName) {
        optionalDiscipline.remove(disciplineName);
    }

    public Iterator<String> iterator() {
        return optionalDiscipline.iterator();
    }

    public void setPath(String path) {
        optionalDiscipline.setPath(path);
    }

    @Override
    public String getPath() {
        return optionalDiscipline.getPath();
    }

    @Override
    public void clear() {
        optionalDiscipline.clear();
    }
}
