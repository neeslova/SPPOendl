package org.example;

import java.util.Scanner;

public class Producer implements Runnable {
    private final TaskQueue taskQueue;
    private final Object confirmationLock;
    private final Scanner scanner;

    public Producer(TaskQueue taskQueue, Object confirmationLock, Scanner scanner) {
        this.taskQueue = taskQueue;
        this.confirmationLock = confirmationLock;
        this.scanner = scanner;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                displayMenu();
                int choice = getValidChoice();

                // Создаем и помещаем задачу в очередь
                taskQueue.put(new Task(choice));

                // Если выбран выход, завершаем работу
                if (choice == 5) {
                    break;
                }

                // Ждем подтверждения выполнения задачи
                waitForConfirmation();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void displayMenu() {
        System.out.println("\nС чем вы хотите работать?");
        System.out.println("1. Направления");
        System.out.println("2. Дисциплины");
        System.out.println("3. Создание траектории обучения");
        System.out.println("4. Изменение существующей");
        System.out.println("5. Выход");
    }

    private int getValidChoice() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Неверный ввод. Пожалуйста, введите число от 1 до 5.");
            }
        }
    }

    private void waitForConfirmation() throws InterruptedException {
        synchronized (confirmationLock) {
            confirmationLock.wait();
        }
    }
}