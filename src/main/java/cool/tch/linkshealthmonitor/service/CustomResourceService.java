package cool.tch.linkshealthmonitor.service;

import cool.tch.linkshealthmonitor.extension.Link;
import cool.tch.linkshealthmonitor.extension.LinkGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.router.selector.FieldSelector;
import run.halo.app.infra.ExternalUrlSupplier;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static cool.tch.linkshealthmonitor.constant.Constant.LINKS_HEALTH_MONITOR;
import static cool.tch.linkshealthmonitor.constant.Constant.LINKS_HEALTH_MONITOR_DESC;
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

    // 获取用户配置的 Halo 外部访问地址
    private final ExternalUrlSupplier externalUrlSupplier;

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
                    log.error("{}【{}】获取所有的友链数据失败: {}", LINKS_HEALTH_MONITOR_DESC, LINKS_HEALTH_MONITOR, error.getMessage(), error);
                    return Mono.just(new ArrayList<>());
                })
                .block();
    }

    /**
     * 根据分组的groupName获取分组的displayName
     * @param groupName 分组的groupName
     * @return 分组的displayName
     */
    public String getGroupDisplayNameByName(String groupName) {
        return client.fetch(LinkGroup.class, groupName)
            .map(linkGroup -> linkGroup.getSpec().getDisplayName())
            .timeout(Duration.ofSeconds(10))
            .onErrorResume(error -> {
                log.error("{}【{}】获取分组数据失败: {}", LINKS_HEALTH_MONITOR_DESC, LINKS_HEALTH_MONITOR, error.getMessage(), error);
                return null;
            })
            .block();
    }

    /**
     * 获取用户配置的 Halo 外部访问地址
     * @return 用户配置的 Halo 外部访问地址
     */
    public String getExternalUrl() {
        // 外部访问地址可以修改，所以不做保存每次都直接查询
        return externalUrlSupplier.getRaw().toString();
    }
}
