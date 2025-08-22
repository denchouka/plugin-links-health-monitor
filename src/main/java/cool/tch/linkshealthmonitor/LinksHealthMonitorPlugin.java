package cool.tch.linkshealthmonitor;

import cool.tch.linkshealthmonitor.task.LinksHealthMonitorResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import run.halo.app.extension.Scheme;
import run.halo.app.extension.SchemeManager;
import run.halo.app.plugin.BasePlugin;
import run.halo.app.plugin.PluginContext;

/**
 * <p>Plugin main class to manage the lifecycle of the plugin.</p>
 * <p>This class must be public and have a public constructor.</p>
 * <p>Only one main class extending {@link BasePlugin} is allowed per plugin.</p>
 *
 * @author Denchouka
 * @since 1.0.0
 */
@Component
@EnableAsync
@EnableScheduling
public class LinksHealthMonitorPlugin extends BasePlugin {

    private final SchemeManager schemeManager;

    public LinksHealthMonitorPlugin(PluginContext pluginContext, SchemeManager schemeManager) {
        super(pluginContext);
        this.schemeManager = schemeManager;
    }

    @Override
    public void start() {
        System.out.println("插件启动成功！");
        // 注册自定义模型
        schemeManager.register(LinksHealthMonitorResult.class);
    }

    @Override
    public void stop() {
        System.out.println("插件停止！");
        // 取消注册自定义模型
        Scheme scheme = schemeManager.get(LinksHealthMonitorResult.class);
        schemeManager.unregister(scheme);
    }
}
