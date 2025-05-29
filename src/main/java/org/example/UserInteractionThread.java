package org.example;

import java.util.Scanner;

public class UserInteractionThread implements Runnable {
    private final SharedState sharedState;

    public UserInteractionThread(SharedState sharedState) {
        this.sharedState = sharedState;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            synchronized (sharedState) {
                if (sharedState.flag != 1) {
                    System.out.println("С чем вы хотите работать?");
                    System.out.println("1. Направления");
                    System.out.println("2. Дисциплины");
                    System.out.println("3. Создание траектории обучения");
                    System.out.println("4. Изменение существующей");
                    System.out.println("5. Выход");

                    String choice = scanner.nextLine();
                    sharedState.choice = Integer.parseInt(choice);
                    sharedState.flag = 1;
                    sharedState.notify(); // Уведомить другой поток о том, что flag был изменен
                } else {
                    try {
                        sharedState.wait(); // Ожидать уведомления от другого потока
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }
}
