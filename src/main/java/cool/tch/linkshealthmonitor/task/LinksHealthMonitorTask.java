package cool.tch.linkshealthmonitor.task;

import cool.tch.linkshealthmonitor.config.LinksHealthMonitorConfig;
import cool.tch.linkshealthmonitor.constant.Constant;
import cool.tch.linkshealthmonitor.extension.Link;
import cool.tch.linkshealthmonitor.extension.LinksHealthMonitorResult;
import cool.tch.linkshealthmonitor.service.CustomResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import run.halo.app.extension.Metadata;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.plugin.ReactiveSettingFetcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cool.tch.linkshealthmonitor.constant.Constant.LINKS_HEALTH_MONITOR;
import static cool.tch.linkshealthmonitor.constant.Constant.LINKS_HEALTH_MONITOR_DESC;

/**
 * @Author Denchouka
 * @Date 2025/8/9 18:04
 * @Desc 定时同步任务
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LinksHealthMonitorTask {

    // 获取插件配置
    private final ReactiveSettingFetcher reactiveSettingFetcher;

    // 与自定义模型交互
    private final ReactiveExtensionClient client;

    // 操作自定义模型
    private final CustomResourceService service;

    private final TaskScheduler taskScheduler;

    private MonitorableScheduledFuture scheduledFuture;


    /**
     * 获取插件配置
     */
    public void getPluginConfig() {
        reactiveSettingFetcher
            .fetch(LinksHealthMonitorConfig.GROUP, LinksHealthMonitorConfig.class)
            .subscribe(this::executeTask, error -> {
                // 配置获取失败
                log.error("{}【{}】配置获取并执行任务失败: {}", LINKS_HEALTH_MONITOR_DESC, LINKS_HEALTH_MONITOR, error.getMessage(), error);
            });
    }

    /**
     * 执行任务
     * @param config 插件配置
     */
    public void executeTask(LinksHealthMonitorConfig config) {
        System.out.println("执行任务执行任务执行任务执行任务执行   cron = " + config.getCustomizedCron());

        // 如果有任务，先停止
        if (scheduledFuture != null) {
            scheduledFuture.stop();
        }

        // 注册新任务
        scheduledFuture = new MonitorableScheduledFuture(
            () -> executeTaskLogic(config),
            taskScheduler,
            // getPractialCron(config)
            "0 0/2 * * * ?"
        );

        // 启动任务
        scheduledFuture.start();
    }

    /**
     * 执行任务逻辑
     * @param config 插件配置
     */
    private void executeTaskLogic(LinksHealthMonitorConfig config) {

        log.info("{}【{}】友链监测开始", LINKS_HEALTH_MONITOR_DESC, LINKS_HEALTH_MONITOR);

        // 自定义模型的对象
        LinksHealthMonitorResult monitorResult = new LinksHealthMonitorResult();
        LinksHealthMonitorResult.ResultSpec resultSpec = new LinksHealthMonitorResult.ResultSpec();
        // 是否启用自定义Cron
        resultSpec.setCustomizedCronEnable(config.isCustomizedCronEnable());
        // 自定义cron
        String customizedCron = config.getCustomizedCron();
        resultSpec.setCustomizedCron(customizedCron);
        // 自定义Cron是否可用
        resultSpec.setCustomizedCronAvailable(LinksHealthMonitorUtils.checkCronExpression(customizedCron));
        // 本站外部地址
        String externalUrl = service.getExternalUrl();
        resultSpec.setExternalUrl(externalUrl);

        // 标准化
        String normalizeUrl = LinksHealthMonitorUtils.normalizeUrl(externalUrl);
        // 本站外部地址不为空时，友链监测记录
        if(StringUtils.isNotBlank(normalizeUrl)) {
            resultSpec.setLinkHealthCheckRecordList(linkHealthCheck(config, resultSpec));
        }

        // 元数据
        Metadata metadata = new Metadata();
        metadata.setName(LinksHealthMonitorUtils.generateMetadataname());
        monitorResult.setMetadata(metadata);

        // 创建自定义模型的对象
        monitorResult.setResultSpec(resultSpec);

        log.info("{}【{}】友链监测结束", LINKS_HEALTH_MONITOR_DESC, LINKS_HEALTH_MONITOR);
        client.create(monitorResult)
            .doOnError(error -> {
                log.error("{}【{}】创建自定义模型的对象失败: {}", LINKS_HEALTH_MONITOR_DESC, LINKS_HEALTH_MONITOR, error.getMessage(), error);
            })
            .subscribe();
    }

    /**
     * 友链监测
     * @param config 插件配置
     * @param resultSpec 自定义模型的对象
     * @return 友链监测记录
     */
    private List<LinksHealthMonitorResult.LinkHealthCheckRecord> linkHealthCheck(
        LinksHealthMonitorConfig config, LinksHealthMonitorResult.ResultSpec resultSpec) {

        // 友链监测记录
        List<LinksHealthMonitorResult.LinkHealthCheckRecord> recordList =
            new ArrayList<>();

        // 查询所有的友链
        List<Link> allLinks = service.getAllLinks();
        // 无需监测友链
        String[] notRequiredMonitorLinks = config.getNotRequiredMonitorLinks();

        // 获取全部的友链页面路由
        String[] allFriendLinkRoutes = LinksHealthMonitorUtils.getAllFriendLinkRoutes(config);

        log.info("{}【{}】友链监测中，无需监测友链为： {}", LINKS_HEALTH_MONITOR_DESC, LINKS_HEALTH_MONITOR, Arrays.toString(notRequiredMonitorLinks));

        // 友链监测
        for (Link link : allLinks) {
            // 获取友链元数据的name，也就是url
            String metaName = link.getMetadata().getName();
            // 友链自定义对象的LinkSpec
            Link.LinkSpec spec = link.getSpec();
            if (StringUtils.isBlank(metaName) || spec == null) {
                continue;
            }

            // 只监测需要监测的友链
            if (notRequiredMonitorLinks == null || notRequiredMonitorLinks.length == 0 || Arrays.stream(notRequiredMonitorLinks).noneMatch(metaName::equals)) {

                // 监测记录
                LinksHealthMonitorResult.LinkHealthCheckRecord checkRecord = new LinksHealthMonitorResult.LinkHealthCheckRecord();

                // 记录友链基本信息
                checkRecord.setLinkName(metaName);
                // 标准化后的友链url
                String url = LinksHealthMonitorUtils.normalizeUrl(spec.getUrl());
                // 虽然新建链接时url就是必须的，也做判空处理（防御性编程，因为模型数据在Data Studio里可以修改）
                if (StringUtils.isBlank(url)) continue;

                checkRecord.setLinkUrl(url);
                String displayName = spec.getDisplayName();
                checkRecord.setLinkDisplayName(displayName);
                String logo = spec.getLogo();
                checkRecord.setLinkLogo(logo);
                // 分组
                String groupName = spec.getGroupName();
                checkRecord.setLinkGroup(groupName);
                checkRecord.setLinkGroupDisplayName(service.getGroupDisplayNameByName(groupName));

                // 功能监测
                // 网站是否可以打开
                checkRecord.setWebsiteAccessible(LinksHealthMonitorUtils.isUrlAccessible(url));
                // 网站logo是否可以访问
                if (StringUtils.isNotBlank(logo)) {
                    checkRecord.setLogoAccessible(LinksHealthMonitorUtils.isUrlAccessible(logo));
                } else {
                    checkRecord.setLogoAccessible(false);
                }
                // 网站名称是否有变更
                LinksHealthMonitorUtils.isDisplayNameChanged(url, displayName, checkRecord);
                // 网站是否包含本站友链
                LinksHealthMonitorUtils.isContainsOurLink(url, resultSpec.getExternalUrl(), allFriendLinkRoutes, checkRecord);
                // 最新更新文章名称，url，更新时间
                LinksHealthMonitorUtils.getLatestArticle(url, checkRecord);

                recordList.add(checkRecord);
            }
        };

        return recordList;
    }

    /**
     * 获取实际执行任务的cron表达式
     * @param config 插件配置
     * @return 新任务的cron表达式
     */
    private String getPractialCron(LinksHealthMonitorConfig config) {
        // 启用自定义cron,并且自定义cron表达式可用时
        String customizedCron = config.getCustomizedCron();
        if (config.isCustomizedCronEnable() && LinksHealthMonitorUtils.checkCronExpression(customizedCron)) {
           return customizedCron;
        }

        return Constant.DEFAULT_CRON;
    }
}
