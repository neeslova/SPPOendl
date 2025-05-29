package org.example;

public class Main {
    public static void main() {
        SharedState sharedState = new SharedState(0, 0);

        UserInteractionThread userInteractionThread = new UserInteractionThread(sharedState);
        ServerTaskThread serverTaskThread = new ServerTaskThread(sharedState);

        Thread userThread = new Thread(userInteractionThread);
        Thread serverThread = new Thread(serverTaskThread);

        userThread.start();
        serverThread.start();
    }
}