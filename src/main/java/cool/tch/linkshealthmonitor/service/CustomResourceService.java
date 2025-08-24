package cool.tch.linkshealthmonitor.service;

import cool.tch.linkshealthmonitor.extension.Link;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.router.selector.FieldSelector;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static cool.tch.linkshealthmonitor.constant.LinksHealthMonitorConstant.LINKS_HEALTH_MONITOR;
import static cool.tch.linkshealthmonitor.constant.LinksHealthMonitorConstant.LINKS_HEALTH_MONITOR_DESC;
import static run.halo.app.extension.index.query.QueryFactory.isNull;

/**
 * @Author Denchouka
 * @Date 2025/8/23 23:03
 * @Desc 操作自定义模型
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomResourceService {

    // 与自定义模型交互
    private final ReactiveExtensionClient client;

    /**
     * 获取所有的友链数据
     * @return 所有的友链数据
     */
    public List<Link> getAllLinks() {
        // 查询所有的友链
        ListOptions listOptions = new ListOptions();
        // 筛选对象metadata.deletionTimestamp为空的，即未被删除的数据
        FieldSelector fieldSelector = FieldSelector.of(isNull("metadata.deletionTimestamp"));
        listOptions.setFieldSelector(fieldSelector);
        return client.listAll(Link.class, listOptions, Sort.by("metadata.creationTimestamp"))
                .collectList()
                .timeout(Duration.ofSeconds(10))
                .onErrorResume(error -> {
                    log.error("{}【{}】配置获取并执行任务失败: {}", LINKS_HEALTH_MONITOR_DESC, LINKS_HEALTH_MONITOR, error.getMessage(), error);
                    return Mono.just(new ArrayList<>());
                })
                .block();
    }
}
