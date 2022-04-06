package org.wso2.stream.invoker;

import java.util.Map;

public class AnalyticsEvent {

    private String streamName;
    private String streamVersion;
    private Map<String, Object> analyticsData;

    public AnalyticsEvent(String streamName, String streamVersion, Map<String, Object> analyticsData) {
        this.streamName = streamName;
        this.streamVersion = streamVersion;
        this.analyticsData = analyticsData;
    }

    public String getStreamName() {

        return streamName;
    }

    public String getStreamVersion() {

        return streamVersion;
    }

    public Map<String, Object> getAnalyticsData() {

        return analyticsData;
    }

}
