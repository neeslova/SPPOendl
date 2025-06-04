package org.example;

import java.util.LinkedList;
import java.util.Queue;

public class TaskQueue {
    private final Queue<Task> queue = new LinkedList<>();
    private final int maxSize;

    public TaskQueue(int maxSize) {
        this.maxSize = maxSize;
    }

    public synchronized void put(Task task) throws InterruptedException {
        while (queue.size() == maxSize) {
            wait();
        }
        queue.add(task);
        notifyAll();
    }

    public synchronized Task take() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        Task task = queue.poll();
        notifyAll();
        return task;
    }
}