package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Создаем очередь задач с ограничением в 10 элементов
        TaskQueue taskQueue = new TaskQueue(10);
        // Объект для синхронизации подтверждения выполнения задач
        Object confirmationLock = new Object();
        Scanner scanner = new Scanner(System.in);
        Menu menu = new Menu();

        // Создаем и запускаем потоки
        Producer producer = new Producer(taskQueue, confirmationLock, scanner);
        Consumer consumer = new Consumer(taskQueue, confirmationLock, menu);

        Thread producerThread = new Thread(producer, "Producer-Thread");
        Thread consumerThread = new Thread(consumer, "Consumer-Thread");

        producerThread.start();
        consumerThread.start();

        try {
            producerThread.join();
            consumerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}