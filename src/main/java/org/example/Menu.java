package org.example;

import org.example.Abstract.DisciplineInterface;
import org.example.Abstract.ProjectInterface;
import org.example.Discipline.DisciplineDecorator;
import org.example.Validator.InputValidatorChain;
import org.example.XML.DisciplinesHandler;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;
import java.util.function.Consumer;

import static org.example.XML.CourseHandler.*;

public class Menu {
    private final Scanner scanner = new Scanner(System.in);

    public void handleDirections() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        InputValidatorChain validatorChain = context.getBean("validator", InputValidatorChain.class);

        Map<String, Runnable> directionActions = new LinkedHashMap<>() {{
            put("1. Добавить курс", () -> {
                addCourseFromUserInput(scanner);
                printCoursesName();
            });
            put("2. Удалить курс", () -> {
                printCourses();
                removeCourseFromUserInput(scanner);
                printCoursesName();
            });
            put("3. Показать курсы", () -> {
                System.out.println();
                printCoursesName();
            });
            put("4. Выйти", () -> {});
        }};

        while (true) {
            System.out.println("\nВыберите действие:");
            directionActions.keySet().forEach(System.out::println);

            String choice = getValidatedInput(validatorChain);
            if (executeAction(choice, directionActions)) break;
        }
        context.close();
    }

    public void handleDisciplines() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        InputValidatorChain validatorChain = context.getBean("validator", InputValidatorChain.class);

        Map<String, Runnable> disciplineActions = new LinkedHashMap<>() {{
            put("1. Добавить дисциплину", () -> {
                System.out.println("Введите название дисциплины для добавления:");
                String disciplineToAdd = scanner.nextLine();
                DisciplinesHandler.addDiscipline(disciplineToAdd);
            });
            put("2. Удалить дисциплину", () -> {
                DisciplinesHandler.printDisciplines();
                removeDisciplineFromUserInput(scanner);
                DisciplinesHandler.printDisciplines();
            });
            put("3. Показать список дисциплин", () -> {
                System.out.println();
                DisciplinesHandler.printDisciplines();
                System.out.println();
            });
            put("4. Выйти", () -> {});
        }};

        while (true) {
            System.out.println("Выберите действие:");
            disciplineActions.keySet().forEach(System.out::println);

            String choice = getValidatedInput(validatorChain);
            if (executeAction(choice, disciplineActions)) break;
        }
        context.close();
    }

    public void handleTrajectoryCreation() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        InputValidatorChain validatorChain = context.getBean("validator", InputValidatorChain.class);

        DisciplineInterface mandatoryDiscipline = context.getBean("mandatoryDiscipline", DisciplineInterface.class);
        DisciplineInterface optionalDiscipline = context.getBean("optionalDiscipline", DisciplineInterface.class);

        printCoursesName();

        int trajectoryChoice;
        List<String> disciplines;
        do {
            System.out.println("Введите номер курса:");
            while (!scanner.hasNextInt()) {
                System.out.println("Невалидный ввод. Попробуйте снова:");
                scanner.next();
            }
            trajectoryChoice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            disciplines = getDisciplinesByCourseNumber(trajectoryChoice);
        } while (disciplines == null);

        disciplines.forEach(mandatoryDiscipline::add);
        System.out.println(mandatoryDiscipline.saveDataToXML());

        System.out.println("\nВыберите дополнительные дисциплины");
        DisciplinesHandler.printDisciplines();

        // Добавление дополнительных дисциплин
        System.out.println("Введите номера дополнительных дисциплин (через пробел) или 'нет' для завершения:");
        String input = scanner.nextLine().trim();

        if (!input.equalsIgnoreCase("нет")) {
            Arrays.stream(input.split("\\s+"))
                    .map(num -> {
                        try {
                            return Integer.parseInt(num);
                        } catch (NumberFormatException e) {
                            return -1;
                        }
                    })
                    .filter(num -> num > 0)
                    .map(DisciplinesHandler::getDisciplineByNumber)
                    .filter(Objects::nonNull)
                    .filter(discipline -> !optionalDiscipline.contains(discipline))
                    .forEach(discipline -> {
                        System.out.println("Добавлена дополнительная дисциплина: " + discipline);
                        optionalDiscipline.add(discipline);
                    });
        }

        printSelectedDisciplines(mandatoryDiscipline, optionalDiscipline);

        Map<String, Consumer<DisciplineInterface>> projectActions = new LinkedHashMap<>() {{
            put("1. Обязательной", disc -> addProjectsToDiscipline(scanner, disc));
            put("2. Дополнительной", disc -> addProjectsToDiscipline(scanner, disc));
            put("3. Остановиться", disc -> {
                // Сохраняем обе дисциплины
                System.out.println(mandatoryDiscipline.saveDataToXML());
                System.out.println(optionalDiscipline.saveDataToXML());

                // Декоратор
                DisciplineInterface decoratedDiscipline1 = context.getBean("decoratedDiscipline1", DisciplineInterface.class);
                DisciplineInterface decoratedDiscipline2 = context.getBean("decoratedDiscipline2", DisciplineInterface.class);
                decoratedDiscipline1 = new DisciplineDecorator(decoratedDiscipline1, decoratedDiscipline2);
                decoratedDiscipline1.saveDataToXML();
            });
        }};

        while (true) {
            System.out.println("\nК какой дисциплине вы хотите добавить проект?");
            projectActions.keySet().forEach(System.out::println);
            System.out.print("Укажите цифру: ");

            String choice = getValidatedInput(validatorChain);
            if (executeDisciplineAction(choice, projectActions, mandatoryDiscipline, optionalDiscipline)) break;
        }

        mandatoryDiscipline.clear();
        optionalDiscipline.clear();
        context.close();
    }

    public void handleTrajectoryModification() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        InputValidatorChain validatorChain = context.getBean("validator", InputValidatorChain.class);

        DisciplineInterface mandatoryDiscipline = context.getBean("mandatoryDiscipline", DisciplineInterface.class);
        DisciplineInterface mandatory = context.getBean("mandatory", DisciplineInterface.class);

        boolean flag_out = false;
        String name = "";

        try {
            boolean fileFound = false;
            while (!fileFound && !flag_out) {
                System.out.println("Введите имя пользователя (или 'нет' для выхода): ");
                name = scanner.nextLine();

                if (name.equalsIgnoreCase("нет")) {
                    flag_out = true;
                    continue;
                }

                String filePath = "resources\\users" + File.separator + name + ".xml";
                if (loadUserData(mandatoryDiscipline, filePath)) {
                    fileFound = true;
                    System.out.println("Данные из файла успешно загружены.");
                    System.out.println(mandatoryDiscipline.saveDataToXML());
                } else {
                    System.out.println("Файл не найден. Попробуйте еще раз.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Произошла ошибка при чтении файла. Попробуйте еще раз.");
        }

        if (flag_out) {
            mandatoryDiscipline.clear();
            return;
        }

        String userName = name; // effectively final копия
        Map<String, Runnable> modificationActions = new LinkedHashMap<>() {{
            put("1. Добавить", () -> handleAddActions(scanner, validatorChain, mandatoryDiscipline, userName, mandatory));
            put("2. Удалить", () -> {
                System.out.println(mandatoryDiscipline.getNames());
                System.out.print("Укажите цифру: ");
                int number = scanner.nextInt();
                scanner.nextLine(); // consume newline
                mandatoryDiscipline.remove(mandatoryDiscipline.getNameNumber(number));
                mandatory.setPath(userName + ".xml");
                mandatory.saveDataToXML();
            });
            put("3. Показать информацию", () -> System.out.println(mandatoryDiscipline.saveDataToXML()));
            put("4. Выход", () -> {});
        }};

        while (true) {
            System.out.println("Вы хотите:");
            modificationActions.keySet().forEach(System.out::println);

            String choice = getValidatedInput(validatorChain);
            if (executeAction(choice, modificationActions)) break;
        }

        mandatoryDiscipline.clear();
        context.close();
    }

    // Вспомогательные методы
    private String getValidatedInput(InputValidatorChain validatorChain) {
        String input = scanner.nextLine();
        while (!validatorChain.validate(input)) {
            input = scanner.nextLine();
        }
        return input;
    }

    private boolean executeAction(String choice, Map<String, Runnable> actions) {
        int index = Integer.parseInt(choice);
        if (index > 0 && index <= actions.size()) {
            actions.values().toArray(new Runnable[0])[index - 1].run();
            return index == actions.size(); // возвращает true если это последний пункт (выход)
        }
        System.out.println("Некорректный выбор. Пожалуйста выберите 1 - " + actions.size());
        return false;
    }

    private boolean executeDisciplineAction(String choice, Map<String, Consumer<DisciplineInterface>> actions,
                                            DisciplineInterface mandatory, DisciplineInterface optional) {
        int index = Integer.parseInt(choice);
        if (index > 0 && index <= actions.size()) {
            DisciplineInterface disc = null;

            if (index < actions.size()) { // Не для последнего пункта
                disc = index == 1 ? mandatory : optional;
            }

            actions.values().toArray(new Consumer[0])[index - 1].accept(disc);
            return index == actions.size(); // возвращает true если это последний пункт
        } else {
            System.out.println("Неверный выбор. Пожалуйста, выберите 1 - " + actions.size());
        }
        return false;
    }

    private static void printSelectedDisciplines(DisciplineInterface mandatory, DisciplineInterface optional) {
        System.out.println("\nВыбранные дисциплины");
        System.out.println("Обязательные дисциплины:");
        int counter = 1;
        for (String discipline : mandatory) {
            System.out.println("\t" + counter + ". " + discipline);
            counter++;
        }

        System.out.println("Дополнительные дисциплины:");
        counter = 1;
        for (String discipline : optional) {
            System.out.println("\t" + counter + ". " + discipline);
            counter++;
        }
    }

    private static boolean loadUserData(DisciplineInterface discipline, String filePath) throws Exception {
        File xmlFile = new File(filePath);
        if (!xmlFile.exists()) return false;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);

        NodeList disciplineList = document.getElementsByTagName("discipline");
        for (int i = 0; i < disciplineList.getLength(); i++) {
            Element disciplineElement = (Element) disciplineList.item(i);
            String disciplineName = disciplineElement.getAttribute("name");
            discipline.add(disciplineName);

            NodeList projectList = disciplineElement.getElementsByTagName("project");
            for (int j = 0; j < projectList.getLength(); j++) {
                Element project = (Element) projectList.item(j);
                String projectName = project.getAttribute("name");

                ProjectInterface projects = discipline.createProject(disciplineName, projectName);
                NodeList taskList = project.getElementsByTagName("task");
                for (int k = 0; k < taskList.getLength(); k++) {
                    Element task = (Element) taskList.item(k);
                    String taskName = task.getAttribute("name");
                    String taskText = task.getTextContent();
                    projects.createTask(projectName, taskText, taskName);
                }
            }
        }
        return true;
    }

    private void handleAddActions(Scanner scanner, InputValidatorChain validatorChain,
                                  DisciplineInterface discipline, String name, DisciplineInterface mandatory) {
        System.out.println("Вы хотите изменить или добавить?");
        System.out.println("1. Добавить проекты и задачи");
        System.out.println("2. Добавить дисциплину");

        String choice = getValidatedInput(validatorChain);
        if (choice.equals("1")) {
            addProjectsToDiscipline(scanner, discipline);
        } else if (choice.equals("2")) {
            System.out.println("Введите название дисциплины: ");
            String disc = scanner.nextLine();
            discipline.setDisciplineInterface(disc);
            System.out.println("Будете ли вы добавлять проекты? ");
            String choice2 = scanner.nextLine();
            if (choice2.equalsIgnoreCase("Да")) {
                addProjectsToDiscipline(scanner, discipline);
            }
        }
        mandatory.setPath(name + ".xml");
        mandatory.saveDataToXML();
    }

    private static void addCourseFromUserInput(Scanner scanner) {
        System.out.println("Введите направление для нового курса:");
        String direction = scanner.nextLine();

        List<String> disciplinesList = new ArrayList<>();
        String discipline;

        System.out.println("Введите дисциплины для нового курса. Введите 'нет', когда закончите:");

        while (true) {
            discipline = scanner.nextLine().trim();
            if ("нет".equalsIgnoreCase(discipline)) {
                break;
            }
            disciplinesList.add(discipline);
        }

        String[] disciplines = disciplinesList.toArray(new String[0]);
        addCourse(direction, disciplines);
    }

    private static void removeCourseFromUserInput(Scanner scanner) {
        System.out.println("Введите номер курса для удаления:");
        int directionToRemove = scanner.nextInt();
        removeCourseByIndex(directionToRemove - 1);
    }

    private static void removeDisciplineFromUserInput(Scanner scanner) {
        System.out.println("Введите номер дисциплины для удаления:");
        int directionToRemove = scanner.nextInt();
        DisciplinesHandler.removeDiscipline(directionToRemove - 1);
    }

    private static void addTaskToProjects(Scanner scanner, ProjectInterface project, String nameProject) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        InputValidatorChain validatorChain = context.getBean("validator", InputValidatorChain.class);
        while (true) {
            System.out.print("\nВведите описание задачи: ");

            String descriptionTask = scanner.nextLine();

            System.out.println("\nЗадача относится к \n\t1.Лабораторной \n\t2.Практике");
            System.out.print("Укажите цифру: ");


            String choice1 = scanner.nextLine();
            while (!validatorChain.validate(choice1)) {
                choice1 = scanner.nextLine();
            }
            if (Integer.parseInt(choice1) == 1) {
                project.createTask(nameProject, descriptionTask, "Лабораторная");
            } else if (Integer.parseInt(choice1) == 2) {
                project.createTask(nameProject, descriptionTask, "Практика");
            }

            System.out.println("""
                                   \s
                Хотите добавить ещё задачи?(Для выхода напишите "нет")""");

            String choice = scanner.nextLine();

            if (choice.equalsIgnoreCase("нет")) {
                break;
            }
        }
        context.close();
    }

    private static void addProjectsToDiscipline(Scanner scanner, DisciplineInterface discipline) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        InputValidatorChain validatorChain = context.getBean("validator", InputValidatorChain.class);
        System.out.println(discipline.getNames());
        System.out.print("Укажите цифру: ");

        String number = scanner.nextLine();
        while (!validatorChain.validate(number)) {
            number = scanner.nextLine();
        }

        while (true) {
            System.out.print("\nВведите название проекта: ");
            String nameProject = scanner.nextLine();
            ProjectInterface project = discipline.createProject(discipline.getNameNumber(Integer.parseInt(number)), nameProject);
            addTaskToProjects(scanner, project, nameProject);


            System.out.println("""
                                   \s
                Хотите добавить ещё проекты?(Для выхода напишите "нет")""");
            String choice = scanner.nextLine();

            if (choice.equalsIgnoreCase("нет")) {
                break;
            }
        }
        context.close();
    }

}
