
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

public class MandatoryDisciplineProxy implements DisciplineInterface {
    private static final Logger logger = Logger.getLogger(MandatoryDisciplineProxy.class.getName());



    private final DisciplineInterface mandatoryDiscipline;

    public MandatoryDisciplineProxy(DisciplineInterface mandatoryDiscipline) {
        this.mandatoryDiscipline = mandatoryDiscipline;
    }

    public Map<String, Project> getData() {
        return mandatoryDiscipline.getData();
    }

    @Override
    public String getNames() {
        return mandatoryDiscipline.getNames();
    }

    @Override
    public String getNameNumber(int number) {
        return mandatoryDiscipline.getNameNumber(number);
    }

    @Override
    public int getQuantity() {
        return mandatoryDiscipline.getQuantity();
    }

    @Override
    public String saveDataToXML() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            Element rootElement = doc.createElement("user");
            doc.appendChild(rootElement);
            logger.fine("Добавление дисциплин");
            Element disciplines = doc.createElement("disciplines");
            rootElement.appendChild(disciplines);
            Map<String, Project> name = mandatoryDiscipline.getData();
            for (Map.Entry<String, Project> entry : name.entrySet()) {
                Project project = entry.getValue();
                Map<String, Task> task = project.getData();
                addDiscipline(doc, disciplines, entry.getKey(), task);
            }
            logger.fine("Запись содержимого в файл");
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);

            // Установка пути к папке "users"
            String usersFolderPath = "resources\\users";
            String fileName = mandatoryDiscipline.getPath();
            String filePath = usersFolderPath + File.separator + fileName;

            // Создание объекта File для папки "users"
            File usersFolder = new File(usersFolderPath);

            // Проверяем, существует ли папка "users", если нет, то создаем её
            if (!usersFolder.exists()) {
                if (usersFolder.mkdirs()) {
                    logger.info("Папка 'users' создана успешно.");
                } else {
                    logger.warning("Не удалось создать папку 'users'.");
                }
            }

            // Продолжаем запись файла только если папка 'users' существует или была успешно создана
            if (usersFolder.exists()) {
                StreamResult result = new StreamResult(new File(filePath));
                transformer.transform(source, result);
                logger.info("XML файл успешно создан.");
            } else {
                logger.warning("Не удалось создать папку 'users', поэтому файл не может быть записан.");
            }
        } catch (Exception e) {
            logger.severe("Ошибка при создании XML файла: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private static void addDiscipline(Document doc, Element parent, String disciplineName, Map<String, Task> projects) {

        try {
            logger.fine("Добавление дисциплины: " + disciplineName);
            Element discipline = doc.createElement("discipline");
            discipline.setAttribute("name", disciplineName);
            parent.appendChild(discipline);

            for (Map.Entry<String, Task> entry : projects.entrySet()) {
                String projectName = entry.getKey();
                Task projectTask = entry.getValue();
                logger.finer("Добавление проекта: " + projectName);
                Element project = doc.createElement("project");
                project.setAttribute("name", projectName);
                discipline.appendChild(project);
                addTask(doc, project, projectTask.getData());
            }
        } catch (Exception e) {
            logger.severe("Ошибка при добавлении дисциплины: " + e.getMessage());
        }
    }

    private static void addTask(Document doc, Element parent, List<String[]> taskNames) {

        try {
            for (String[] names : taskNames) {
                logger.finest("Добавление задачи: " + names[0]);
                Element task = doc.createElement("task");
                String taskName = names[0];
                task.setAttribute("name", taskName);
                task.appendChild(doc.createTextNode(names[1]));
                parent.appendChild(task);
            }
        } catch (Exception e) {
            logger.severe("Ошибка при добавлении задачи: " + e.getMessage());
        }
    }

    @Override
    public void add(String name) {
        mandatoryDiscipline.add(name);
    }

    public ProjectInterface createProject(String discipline, String name) {
        mandatoryDiscipline.createProject(discipline, name);
        return null;
    }

    @Override
    public boolean contains(String name) {
        return mandatoryDiscipline.contains(name);
    }

    public void setDiscipline(String disciplineName) {
        mandatoryDiscipline.setDiscipline(disciplineName);
    }

    public void remove(String disciplineName) {
        mandatoryDiscipline.remove(disciplineName);
    }

    public Iterator<String> iterator() {
        return mandatoryDiscipline.iterator();
    }

    public void setPath(String path) {
        mandatoryDiscipline.setPath(path);
    }

    @Override
    public String getPath() {
        return mandatoryDiscipline.getPath();
    }

    @Override
    public void clear() {
        mandatoryDiscipline.clear();
    }
}


