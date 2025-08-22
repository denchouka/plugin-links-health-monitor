package cool.tch.linkshealthmonitor.listener;

import cool.tch.linkshealthmonitor.task.LinksHealthMonitorTaskScheduler;
import cool.tch.linkshealthmonitor.config.LinksHealthMonitorConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.PluginConfigUpdatedEvent;

/**
 * @Author Denchouka
 * @Date 2025/8/9 15:00
 * @Desc 插件的配置监听器
 */
@Component
@RequiredArgsConstructor
public class LinksHealthMonitorListener {

    private final LinksHealthMonitorTaskScheduler linksHealthMonitorAsync;

    /**
     * 监听插件配置变更
     * @param event 插件变更事件
     */
    @EventListener
    public void onConfigUndated(PluginConfigUpdatedEvent event) {
        if (event.getNewConfig().containsKey(LinksHealthMonitorConfig.GROUP)) {
            // 监听到插件配置变更
            linksHealthMonitorAsync.getPluginConfig();
        }
    }

}
