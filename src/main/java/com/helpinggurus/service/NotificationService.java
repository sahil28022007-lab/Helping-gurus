package com.helpinggurus.service;

import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Background worker thread. Request threads only enqueue a message (fast); this single consumer
 * thread "sends" the thank-you email (simulated by logging). Producer-consumer with a BlockingQueue.
 */
public final class NotificationService {
    private static final Logger LOG = Logger.getLogger("notifications");
    private static final BlockingQueue<String> QUEUE = new LinkedBlockingQueue<>();
    private static Thread worker;

    private NotificationService() {}

    /** Starts the single worker thread (safe to call more than once). */
    public static synchronized void start() {
        if (worker != null) return;
        worker = new Thread(() -> {
            try { while (!Thread.currentThread().isInterrupted()) LOG.info("[mail] " + QUEUE.take()); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }, "notification-worker");
        worker.setDaemon(true);
        worker.start();
    }
    /** Called by request threads: adds a message to the queue and returns immediately. */
    public static void enqueue(String message) { QUEUE.offer(message); }
    /** Stops the worker thread when the application shuts down. */
    public static synchronized void stop() { if (worker != null) { worker.interrupt(); worker = null; } }
}
