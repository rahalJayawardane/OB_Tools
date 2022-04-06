package org.wso2.stream.invoker;

import java.util.Map;

public interface DataPublisher {

    void publish(String streamName, String streamVersion, Map<String, Object> analyticsData);

}
