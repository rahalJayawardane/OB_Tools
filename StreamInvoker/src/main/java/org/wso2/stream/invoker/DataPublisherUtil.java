package org.wso2.stream.invoker;

import java.util.Map;

public class DataPublisherUtil {

    public static DataPublisher getDataPublisherInstance() {

        DataPublisherPool<DataPublisher> pool =
                AnalyticsDataHolder.getInstance().getDataPublisherPool();
        try {
            return pool.borrowObject();
        } catch (Exception e) {
            log.error("Error while receiving Thrift Data Publisher from the pool.");
        }
        return null;
    }

    /**
     * Util method to publish OB analytics data.
     * This method will put received data to an event queue and take care of asynchronous data publishing.
     */
    public static void publishData(String streamName, String streamVersion, Map<String, Object> analyticsData) {

        EventQueue eventQueue = AnalyticsDataHolder.getInstance().getEventQueue();
        if (!(eventQueue == null)) {
            AnalyticsEvent event = new AnalyticsEvent(streamName, streamVersion, analyticsData);
            eventQueue.put(event);
        } else {
            System.out.println("Unable to get the event queue. Data publishing may be disabled.");
        }
    }

}
