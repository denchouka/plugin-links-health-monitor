package cool.tch.linkshealthmonitor;

import cool.tch.linkshealthmonitor.task.LinksHealthMonitorUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import run.halo.app.extension.SchemeManager;
import run.halo.app.plugin.PluginContext;

@ExtendWith(MockitoExtension.class)
class LinksHealthMonitorPluginTest {

    @Mock
    PluginContext context;

    @InjectMocks
    LinksHealthMonitorPlugin plugin;

    @Mock
    SchemeManager schemeManager;

    @Test
    void contextLoads() {
        plugin.start();
        plugin.stop();
    }
}
