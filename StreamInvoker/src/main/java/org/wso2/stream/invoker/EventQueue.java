package org.wso2.stream.invoker;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class EventQueue {

    private final BlockingQueue<AnalyticsEvent> eventQueue;
    private final ExecutorService publisherExecutorService;

    public EventQueue(int queueSize, int workerThreadCount) {

        // Note : Using a fixed worker thread pool and a bounded queue to control the load on the server
        publisherExecutorService = Executors.newFixedThreadPool(workerThreadCount);
        eventQueue = new ArrayBlockingQueue<>(queueSize);
    }

    public void put(AnalyticsEvent obAnalyticsEvent) {

        try {
            if (eventQueue.offer(obAnalyticsEvent)) {
                publisherExecutorService.submit(new QueueWorker(eventQueue, publisherExecutorService));
            } else {
                System.out.println("Event queue is full. Starting to drop OB analytics events.");
            }
        } catch (RejectedExecutionException e) {
            System.out.println("Task submission failed. Task queue might be full - " + e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        publisherExecutorService.shutdown();
        super.finalize();
    }

}
