package org.wso2.stream.invoker;

import java.util.Map;

public class AnalyticsDataHolder {
    private static volatile AnalyticsDataHolder instance;
    private Map<String, Object> configurationMap;
    private DataPublisherPool<DataPublisher> pool;
    private int poolSize;
    private EventQueue eventQueue;

    public static AnalyticsDataHolder getInstance() {

        if (instance == null) {
            synchronized (AnalyticsDataHolder.class) {
                if (instance == null) {
                    instance = new AnalyticsDataHolder();
                }
            }
        }
        return instance;
    }

    public Map<String, Object> getConfigurationMap() {

        return configurationMap;
    }

}
