package com.kkalinski;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.Date;
import java.util.List;

public class PermGenReader {
    private MemoryPoolMXBean permgenBean = null;

    public PermGenReader() {
        initMonitorPermGen();
    }
    private void initMonitorPermGen() {
        List<MemoryPoolMXBean> beans =
                ManagementFactory.getMemoryPoolMXBeans();
        for(MemoryPoolMXBean bean : beans) {
            if(bean.getName().toLowerCase().indexOf("perm gen") >= 0) {
                permgenBean = bean;
                break;
            }
        }
    }

    public MemoryUsage getUsage() {
        return permgenBean.getUsage();
    }

    public String toString() {
        long max = getMax();
        int percentageUsed = (int)((getUsage().getUsed() * 100)
                / max);
        String message = "%TT: Permgen  %d of %d  ( %d %% )";
        return String.format(message, new Date(), getUsage().getUsed(), max, percentageUsed );

    }

    private long getMax() {
        MemoryUsage currentUsage = getUsage();
        return currentUsage.getMax();
    }
}
