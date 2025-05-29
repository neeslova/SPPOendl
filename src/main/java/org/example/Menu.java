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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import static org.example.XML.CourseHandler.*;


public class Menu {

    Scanner scanner = new Scanner(System.in);

    public void handleDirections() {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        InputValidatorChain validatorChain = context.getBean("validator", InputValidatorChain.class);

        // Определение фабрик
        DisciplineInterface mandatoryDiscipline = context.getBean("mandatoryDiscipline" ,DisciplineInterface.class);
        DisciplineInterface optionalDiscipline = context.getBean("optionalDiscipline" ,DisciplineInterface.class);

        // Здесь код обработки направления (курсы)
        // Работа с Курсами
        while (true) {
            System.out.println("\nВыберите действие:");
            System.out.println("1. Добавить курс");
            System.out.println("2. Удалить курс");
            System.out.println("3. Показать курсы");
            System.out.println("4. Выйти");

            //Проверка выбора "Выберите действие:"
            String choice = scanner.nextLine();
            while (!validatorChain.validate(choice)) {
                choice = scanner.nextLine();
            }

            switch (Integer.parseInt(choice)) {
                case 1:
                    addCourseFromUserInput(scanner);
                    printCoursesName();
                    continue;
                case 2:
                    printCourses();
                    removeCourseFromUserInput(scanner);
                    printCoursesName();
                    continue;
                case 3:
                    System.out.println();
                    printCoursesName();
                    continue;
                case 4:
                    break;
                default:
                    System.out.println("Некорректный выбор. Пожалуйста выберите 1 - 5.");
            }
            break;
        }
        context.close();

    }

    public void handleDisciplines() {
        // Здесь код обработки дисциплин
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        InputValidatorChain validatorChain = context.getBean("validator", InputValidatorChain.class);

        // Определение фабрик
        DisciplineInterface mandatoryDiscipline = context.getBean("mandatoryDiscipline" ,DisciplineInterface.class);
        DisciplineInterface optionalDiscipline = context.getBean("optionalDiscipline" ,DisciplineInterface.class);

        while (true) {
            System.out.println("Выберите действие:");
            System.out.println("1. Добавить дисциплину");
            System.out.println("2. Удалить дисциплину");
            System.out.println("3. Показать список дисциплин");
            System.out.println("4. Выйти");

            //Проверка выбора "Выберите действие:"
            String choice = scanner.nextLine();
            while (!validatorChain.validate(choice)) {
                choice = scanner.nextLine();
            }

            switch (Integer.parseInt(choice)) {
                case 1:
                    System.out.println("Введите название дисциплины для добавления:");
                    String disciplineToAdd = scanner.nextLine();
                    DisciplinesHandler.addDiscipline(disciplineToAdd);
                    continue;
                case 2:
                    DisciplinesHandler.printDisciplines();
                    removeDisciplineFromUserInput(scanner);
                    DisciplinesHandler.printDisciplines();
                    continue;
                case 3:
                    System.out.println();
                    DisciplinesHandler.printDisciplines();
                    System.out.println();
                    continue;
                case 4:
                    break;
                default:
                    System.out.println("Неверный выбор. Пожалуйста выберите 1, 2, 3 или 4.");
            }
            break;
        }
        context.close();
    }

    public void handleTrajectoryCreation() {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        InputValidatorChain validatorChain = context.getBean("validator", InputValidatorChain.class);

        // Определение фабрик
        DisciplineInterface mandatoryDiscipline = context.getBean("mandatoryDiscipline" ,DisciplineInterface.class);
        DisciplineInterface optionalDiscipline = context.getBean("optionalDiscipline" ,DisciplineInterface.class);

        // Здесь код создания траектории обучения
        // Добавление обязательных дисциплин
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
            disciplines = getDisciplinesByCourseNumber(trajectoryChoice);
        } while (disciplines == null);

        for (String discipline : disciplines) {
            mandatoryDiscipline.add(discipline);
        }
        System.out.println(mandatoryDiscipline.saveDataToXML());


        // Работа с дополнительными дисциплинами
        System.out.println("\nВыберите дополнительные дисциплины");
        DisciplinesHandler.printDisciplines();

        // Добавление дополнительных дисциплин
        while (true) {
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("нет")) {
                break;
            }

            if (input.isEmpty()) {
                System.out.println("Пожалуйста, введите номер дисциплины или 'нет' для завершения.");
                continue;
            }

            try {
                int number = Integer.parseInt(input);
                String disciplineWithRoot = DisciplinesHandler.getDisciplineByNumber(number);

                if (disciplineWithRoot != null) {
                    System.out.println("Добавлена дополнительная дисциплина: " + disciplineWithRoot);

                    if (optionalDiscipline.contains(disciplineWithRoot)) {
                        System.out.println("Дисциплина уже добавлена. Пожалуйста, введите другую.");
                        continue;
                    }

                    optionalDiscipline.add(disciplineWithRoot);
                }
                else{
                    System.out.println("Неверный ввод");
                }
            } catch (NumberFormatException e) {
                System.out.println("Некорректный формат ввода. Пожалуйста, введите номер дисциплины или 'нет' для завершения.");
            }
        }

        // Вывод списка дисциплин
        System.out.println("\nВыбранные дисциплины");
        System.out.println("Обязательные дисциплины:");
        int counter = 1;
        for (String discipline : mandatoryDiscipline) {
            System.out.println("\t" + counter + ". " + discipline);
            counter++;
        }

        System.out.println("Дополнительные дисциплины:");
        counter = 1; // сброс счетчика
        for (String discipline : optionalDiscipline) {
            System.out.println("\t" + counter + ". " + discipline);
            counter++;
        }


        // Работа с проектами и задачами
        while (true) {
            System.out.println("""
                           \s
                        К какой дисциплине вы хотите добавить проект?
                        1. Обязательной
                        2. Дополнительной
                        3. Остановиться""");
            System.out.print("Укажите цифру: ");

            //Проверка выбора "К какой дисциплине вы хотите добавить проект?"
            String number = scanner.nextLine();
            while (!validatorChain.validate(number)) {
                number = scanner.nextLine();
            }

            switch (Integer.parseInt(number)) {
                case 1:
                    addProjectsToDiscipline(scanner, mandatoryDiscipline);
                    continue;
                case 2:
                    addProjectsToDiscipline(scanner, optionalDiscipline);
                    continue;
                case 3:
                    System.out.println(mandatoryDiscipline.saveDataToXML());
                    System.out.println(optionalDiscipline.saveDataToXML());

                    DisciplineInterface decoratedDiscipline1 = context.getBean("decoratedDiscipline1", DisciplineInterface.class );
                    DisciplineInterface decoratedDiscipline2 = context.getBean("decoratedDiscipline2", DisciplineInterface.class );
                    decoratedDiscipline1 = new DisciplineDecorator(decoratedDiscipline1, decoratedDiscipline2);
                    decoratedDiscipline1.saveDataToXML();
                    break;
                default:
                    System.out.println("Неверный выбор. Пожалуйста, выберите 1,2 или 3.");
                    continue;
            }
            break;
        }

        mandatoryDiscipline.clear();
        optionalDiscipline.clear();

        context.close();
    }

    public void handleTrajectoryModification() {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        InputValidatorChain validatorChain = context.getBean("validator", InputValidatorChain.class);

        // Определение фабрик
        DisciplineInterface mandatoryDiscipline = context.getBean("mandatoryDiscipline" ,DisciplineInterface.class);
        DisciplineInterface optionalDiscipline = context.getBean("optionalDiscipline" ,DisciplineInterface.class);

        // Здесь код изменения существующей траектории
        boolean flag_out = false;
        String name = "";
        // Считывание данных
        try {
            boolean fileFound = false;


            while (!fileFound) {
                System.out.println("Введите имя пользователя (или 'нет' для выхода): ");
                name = scanner.nextLine();

                if (name.equalsIgnoreCase("нет")) {
                    flag_out = true;
                    break;
                }

                String filePath = "resources\\users" + File.separator + name + ".xml";
                File xmlFile = new File(filePath);

                if (xmlFile.exists()) {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.parse(xmlFile);

                    NodeList disciplineList = document.getElementsByTagName("discipline");
                    for (int i = 0; i < disciplineList.getLength(); i++) {
                        Element discipline = (Element) disciplineList.item(i);
                        String disciplineName = discipline.getAttribute("name");

                        mandatoryDiscipline.add(disciplineName);

                        NodeList projectList = discipline.getElementsByTagName("project");
                        for (int j = 0; j < projectList.getLength(); j++) {
                            Element project = (Element) projectList.item(j);
                            String projectName = project.getAttribute("name");

                            ProjectInterface projects = mandatoryDiscipline.createProject(disciplineName, projectName);
                            NodeList taskList = project.getElementsByTagName("task");
                            for (int k = 0; k < taskList.getLength(); k++) {
                                Element task = (Element) taskList.item(k);
                                String taskName = task.getAttribute("name");
                                String taskText = task.getTextContent();

                                projects.createTask(projectName, taskText, taskName);
                            }
                        }
                    }

                    System.out.println("Данные из файла успешно загружены.");
                    System.out.println(mandatoryDiscipline.saveDataToXML());

                    fileFound = true;
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

        DisciplineInterface mandatory = context.getBean("mandatory" ,DisciplineInterface.class);

        while (true) {
            System.out.println("Вы хотите:");
            System.out.println("1. Добавить");
            System.out.println("2. Удалить");
            System.out.println("3. Показать информацию");
            System.out.println("4. Выход");

            //Проверка выбора "Вы хотите:"
            String choice = scanner.nextLine();
            while (!validatorChain.validate(choice)) {
                choice = scanner.nextLine();
            }

            if (Integer.parseInt(choice) == 1) {
                System.out.println("Вы хотите изменить или добавить?");
                System.out.println("1. Добавить проекты и задачи");
                System.out.println("2. Добавить дисциплину");

                //Проверка выбора "Вы хотите изменить или добавить?"
                String choice1 = scanner.nextLine();
                while (!validatorChain.validate(choice1)) {
                    choice1 = scanner.nextLine();
                }

                if (Integer.parseInt(choice1) == 1) {
                    addProjectsToDiscipline(scanner, mandatoryDiscipline);
                }
                if (Integer.parseInt(choice1) == 2) {
                    System.out.println("Введите название дисциплины: ");
                    String disc = scanner.nextLine();
                    mandatoryDiscipline.setDiscipline(disc);
                    System.out.println("Будете ли вы добавлять проекты? ");
                    String choice2 = scanner.nextLine();
                    if (Objects.equals(choice2, "Да")) {
                        addProjectsToDiscipline(scanner, mandatoryDiscipline);
                    }
                }

                mandatory.setPath(name + ".xml");
                mandatory.saveDataToXML();
            }

            if (Integer.parseInt(choice) == 2) {
                System.out.println(mandatoryDiscipline.getNames());
                System.out.print("Укажите цифру: ");
                int number = scanner.nextInt();
                mandatoryDiscipline.remove(mandatoryDiscipline.getNameNumber(number));
                scanner.nextLine();
                mandatory.setPath(name + ".xml");
                mandatory.saveDataToXML();
            }
            if (Integer.parseInt(choice) == 3) {
                System.out.println(mandatoryDiscipline.saveDataToXML());
            }
            if (Integer.parseInt(choice) == 4) {
                break;
            }
        }
        mandatoryDiscipline.clear();

        context.close();
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
