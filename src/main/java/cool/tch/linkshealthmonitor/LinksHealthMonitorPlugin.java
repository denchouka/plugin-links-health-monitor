package cool.tch.linkshealthmonitor;

import cool.tch.linkshealthmonitor.extension.LinksHealthMonitorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.halo.app.extension.Scheme;
import run.halo.app.extension.SchemeManager;
import run.halo.app.plugin.BasePlugin;
import run.halo.app.plugin.PluginContext;

import static cool.tch.linkshealthmonitor.constant.Constant.LINKS_HEALTH_MONITOR;
import static cool.tch.linkshealthmonitor.constant.Constant.LINKS_HEALTH_MONITOR_DESC;

/**
 * <p>Plugin main class to manage the lifecycle of the plugin.</p>
 * <p>This class must be public and have a public constructor.</p>
 * <p>Only one main class extending {@link BasePlugin} is allowed per plugin.</p>
 *
 * @author Denchouka
 * @since 1.0.0
 */
@Component
@Slf4j
public class LinksHealthMonitorPlugin extends BasePlugin {

    private final SchemeManager schemeManager;

    public LinksHealthMonitorPlugin(PluginContext pluginContext, SchemeManager schemeManager) {
        super(pluginContext);
        this.schemeManager = schemeManager;
    }

    @Override
    public void start() {
        log.info("{}【{}】启动", LINKS_HEALTH_MONITOR_DESC, LINKS_HEALTH_MONITOR);
        // 注册自定义模型
        schemeManager.register(LinksHealthMonitorResult.class);
    }

    @Override
    public void stop() {
        log.info("{}【{}】停止", LINKS_HEALTH_MONITOR_DESC, LINKS_HEALTH_MONITOR);
        // 取消注册自定义模型
        Scheme scheme = schemeManager.get(LinksHealthMonitorResult.class);
        schemeManager.unregister(scheme);
    }
}
