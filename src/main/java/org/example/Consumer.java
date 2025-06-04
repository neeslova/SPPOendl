package org.example;

import java.util.HashMap;
import java.util.Map;

public class Consumer implements Runnable {
    private final TaskQueue taskQueue;
    private final Object confirmationLock;
    private final Menu menu;
    private final Map<Integer, Runnable> choiceHandlers;

    public Consumer(TaskQueue taskQueue, Object confirmationLock, Menu menu) {
        this.taskQueue = taskQueue;
        this.confirmationLock = confirmationLock;
        this.menu = menu;

        this.choiceHandlers = new HashMap<>();
        initializeHandlers();
    }

    private void initializeHandlers() {
        choiceHandlers.put(1, menu::handleDirections);
        choiceHandlers.put(2, menu::handleDisciplines);
        choiceHandlers.put(3, menu::handleTrajectoryCreation);
        choiceHandlers.put(4, menu::handleTrajectoryModification);
        choiceHandlers.put(5, () -> {});
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Task task = taskQueue.take();
                int choice = task.getChoice();

                handleUserChoice(choice);

                if (choice == 5) {
                    System.out.println("Завершение работы приложения...");
                    System.exit(0);
                }

                notifyProducer();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void handleUserChoice(int choice) {
        choiceHandlers.getOrDefault(choice,
                () -> System.out.println("Неверный выбор. Пожалуйста выберите 1, 2, 3, 4 или 5.")
        ).run();
    }

    private void notifyProducer() {
        synchronized (confirmationLock) {
            confirmationLock.notify();
        }
    }
}