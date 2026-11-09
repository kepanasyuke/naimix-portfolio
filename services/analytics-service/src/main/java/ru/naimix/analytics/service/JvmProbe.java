package ru.naimix.analytics.service;

import java.lang.management.ManagementFactory;
import org.springframework.stereotype.Component;
import ru.naimix.analytics.api.dto.JvmView;

/**
 * Reads JVM runtime metrics without external agents.
 */
@Component
public class JvmProbe {

    /**
     * Takes a snapshot of heap and thread usage.
     *
     * @return metrics snapshot
     */
    public JvmView snapshot() {
        final Runtime runtime = Runtime.getRuntime();
        return new JvmView(runtime.totalMemory() - runtime.freeMemory(), runtime.totalMemory(),
                runtime.maxMemory(), ManagementFactory.getThreadMXBean().getThreadCount(),
                ManagementFactory.getRuntimeMXBean().getUptime());
    }
}
