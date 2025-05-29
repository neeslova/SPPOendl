package org.example;

public class SharedState {
    public volatile int choice;
    public volatile int flag;

    public SharedState(int choice, int flag) {
        this.choice = choice;
        this.flag = flag;
    }
}
