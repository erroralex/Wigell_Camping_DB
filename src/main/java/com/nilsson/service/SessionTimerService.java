package com.nilsson.service;

import com.nilsson.ui.CustomTitleBar;
import javafx.application.Platform;

public class SessionTimerService implements Runnable {

    private final CustomTitleBar view;
    private long totalMillis = 0;
    private volatile boolean running = false;
    private volatile boolean stopRequested = false;

    public SessionTimerService(CustomTitleBar view) {
        this.view = view;
    }

    @Override
    public void run() {
        running = true;
        stopRequested = false;
        long startTime = System.currentTimeMillis();

        while (running && !stopRequested) {
            totalMillis = System.currentTimeMillis() - startTime;

            // Calculate time
            long hours = (totalMillis / 3600000) % 60;
            long minutes = (totalMillis / 60000) % 60;
            long seconds = (totalMillis / 1000) % 60;

            // Update UI on the JavaFX Application Thread
            String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            Platform.runLater(() -> view.timeDisplayLabel.setText(timeString));

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        running = false;
        System.out.println("Timer thread stopped.");
    }

    // Starts the timer by creating and running a new Thread
    public void start() {
        if (!running) {
            new Thread(this, "SessionTimerThread").start();
            System.out.println("Session timer is running in thread " + Thread.currentThread().getName());
        }
    }

    // Stop the timer thread
    public void stop() {
        if (running) {
            stopRequested = true;
        }
    }

    public void reset() {
        stop();
        totalMillis = 0;
        Platform.runLater(() -> view.timeDisplayLabel.setText("00:00:00"));
    }
}