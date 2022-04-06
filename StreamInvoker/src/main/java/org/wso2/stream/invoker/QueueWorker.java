package org.wso2.stream.invoker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public class QueueWorker {

    private BlockingQueue<AnalyticsEvent> eventQueue;
    private ExecutorService executorService;

    public QueueWorker(BlockingQueue<AnalyticsEvent> queue, ExecutorService executorService) {

        this.eventQueue = queue;
        this.executorService = executorService;
    }

    public void run() {

        ThreadPoolExecutor threadPoolExecutor = ((ThreadPoolExecutor) executorService);

        do {
            AnalyticsEvent event = eventQueue.poll();
            if (event != null) {
                DataPublisher dataPublisher = DataPublisherUtil.getDataPublisherInstance();
                if (dataPublisher != null) {
                    dataPublisher.publish(event.getStreamName(), event.getStreamVersion(), event.getAnalyticsData());
                    DataPublisherUtil.releaseDataPublishingInstance(dataPublisher);
                }
            } else {
                System.out.println("OB Analytics Event is null");
            }
        } while (threadPoolExecutor.getActiveCount() == 1 && eventQueue.size() != 0);
    }

}
