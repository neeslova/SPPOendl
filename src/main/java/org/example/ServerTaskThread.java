package org.example;

public class ServerTaskThread implements Runnable {
    private final SharedState sharedState;

    public ServerTaskThread(SharedState sharedState) {
        this.sharedState = sharedState;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (sharedState) {
                if (sharedState.flag == 1) {
                    handleUserChoice(String.valueOf(sharedState.choice));
                    sharedState.flag = 0;
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

    private void handleUserChoice(String choice) {
        Menu menu = new Menu();
        switch (choice) {
            case "1":
                menu.handleDirections();
                break;
            case "2":
                menu.handleDisciplines();
                break;
            case "3":
                menu.handleTrajectoryCreation();
                break;
            case "4":
                menu.handleTrajectoryModification();
                break;
            case "5":
                System.exit(0);
            default:
                System.out.println("Неверный выбор. Пожалуйста выберите 1, 2, 3, 4 или 5.");
        }
    }
}
