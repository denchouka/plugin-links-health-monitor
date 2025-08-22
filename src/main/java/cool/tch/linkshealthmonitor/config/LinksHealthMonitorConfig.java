package cool.tch.linkshealthmonitor.config;

import lombok.Data;

@Data
public class LinksHealthMonitorConfig {

    public static final String GROUP = "basic";

    // 是否自定义Cron
    private boolean customizedCronEnable;

    // 自定义cron
    private String customizedCron;

    // 无需监测友链
    private String[] notRequierdMonitorlinks;
}
