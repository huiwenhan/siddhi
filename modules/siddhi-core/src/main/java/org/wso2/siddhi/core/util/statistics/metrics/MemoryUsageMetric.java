package org.wso2.siddhi.core.util.statistics.metrics;

import com.codahale.metrics.Gauge;
import org.wso2.siddhi.core.util.statistics.MemoryUsageTracker;
import org.wso2.siddhi.core.util.statistics.memory.measurer.MemoryMeasurerUtil;
import org.wso2.siddhi.core.util.statistics.memory.measurer.ObjectGraphMeasurer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by sajith on 11/26/15.
 */
public class MemoryUsageMetric implements MemoryUsageTracker {
    private ConcurrentMap<Object, ObjectMetric> registeredObjects = new ConcurrentHashMap<Object, ObjectMetric>();
    private  MetricRegistryHolder metricRegistry = null;

    /**
     * Register the object that needs to be measured the memory usage
     *
     * @param object Object
     * @param name   An unique value to identify the object.
     */
    @Override
    public void registerObject(Object object, String name) {
        if (registeredObjects.get(object) == null) {
            registeredObjects.put(object, new ObjectMetric(object, name + ".memory"));
        }
    }

    /**
     * @return Name of the memory usage tracker.
     */
    @Override
    public String getName(Object object) {
        if (registeredObjects.get(object) != null) {
            return registeredObjects.get(object).getName();
        } else {
           return null;
        }
    }

    public void init(MetricRegistryHolder registryHolder){
        this.metricRegistry = registryHolder;
    }

    class ObjectMetric {

        private final Object object;
        private String name;

        public ObjectMetric(final Object object, String name){
            this.object = object;
            this.name = name;
            initMetric();
        }

        public String getName() { return name;}

        private void initMetric(){
            metricRegistry.getRegistry().register(name ,
                    new Gauge<Long>() {
                        @Override
                        public Long getValue() {
                            ObjectGraphMeasurer.Footprint footprint = ObjectGraphMeasurer.measure(object);
                            return MemoryMeasurerUtil.FootprintSizeEstimate(footprint);
                        }
                    });
        }
    }
    
}