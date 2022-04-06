package org.wso2.stream.invoker;

import org.apache.tomcat.dbcp.pool2.PooledObjectFactory;
import org.apache.tomcat.dbcp.pool2.impl.GenericObjectPoolConfig;

public class DataPublisherPool {

    public DataPublisherPool(PooledObjectFactory<DataPublisher> factory,
                             GenericObjectPoolConfig<DataPublisher> config) {

        super(factory, config);
    }

}
