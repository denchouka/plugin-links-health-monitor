package cool.tch.linkshealthmonitor.task;

import cool.tch.linkshealthmonitor.config.LinksHealthMonitorConfig;
import cool.tch.linkshealthmonitor.extension.Link;
import cool.tch.linkshealthmonitor.extension.LinkMetadataAnnotations;
import cool.tch.linkshealthmonitor.extension.LinksHealthMonitorResult;
import cool.tch.linkshealthmonitor.service.CustomResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import run.halo.app.extension.Metadata;
import run.halo.app.extension.MetadataOperator;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.plugin.ReactiveSettingFetcher;

import java.util.ArrayList;
import java.util.List;

import static cool.tch.linkshealthmonitor.constant.Constant.DEFAULT_CRON;
import static cool.tch.linkshealthmonitor.constant.Constant.DEFAULT_CRON_DESC;
import static cool.tch.linkshealthmonitor.constant.Constant.LINKS_HEALTH_MONITOR;
import static cool.tch.linkshealthmonitor.constant.Constant.LINKS_HEALTH_MONITOR_DESC;
import static cool.tch.linkshealthmonitor.extension.LinkMetadataAnnotations.fromMap;
import static cool.tch.linkshealthmonitor.task.MonitorableScheduledFuture.TaskStatus.UNCREATED;

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

    // 友链总数
    private int allLinkCount = 0;
    // 无需监测友链总数
    private int notRequiredLinkCount = 0;
    // 已监测友链总数
    private int monitoredLinkCount = 0;


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
        // 如果有任务，先停止
        if (scheduledFuture != null) {
            scheduledFuture.stop();
        }

        // 注册新任务
        scheduledFuture = new MonitorableScheduledFuture(
            () -> executeTaskLogic(config),
            taskScheduler,
            getPractialCron(config)
        );

        // 启动任务
        scheduledFuture.start();
    }

    /**
     * 获取任务执行信息
     * @return 任务执行信息
     */
    public MonitorableScheduledFuture.TaskInfo getTaskExecuteInfo() {
        // 任务未创建
        if (scheduledFuture == null) {
            MonitorableScheduledFuture.TaskInfo taskInfo = new MonitorableScheduledFuture.TaskInfo(UNCREATED.getValue());
            return taskInfo;
        }

        return scheduledFuture.getTaskInfo(allLinkCount, notRequiredLinkCount, monitoredLinkCount);
    }

    /**
     * 执行任务逻辑
     * @param config 插件配置
     */
    private void executeTaskLogic(LinksHealthMonitorConfig config) {

        // 自定义模型的对象
        LinksHealthMonitorResult monitorResult = new LinksHealthMonitorResult();
        LinksHealthMonitorResult.ResultSpec resultSpec = new LinksHealthMonitorResult.ResultSpec();
        // 是否启用自定义Cron
        resultSpec.setCustomizedCronEnable(config.isCustomizedCronEnable());
        // 自定义cron
        String customizedCron = config.getCustomizedCron();
        // 自定义Cron是否可用
        resultSpec.setCustomizedCronAvailable(LinksHealthMonitorUtils.checkCronExpression(customizedCron));
        // 执行任务的Cron表达式
        String cronExpression = getPractialCron(config);
        resultSpec.setCronExpression(DEFAULT_CRON.equals(cronExpression) ? cronExpression + DEFAULT_CRON_DESC : cronExpression);
        // 本站外部地址
        String externalUrl = service.getExternalUrl();

        // 标准化
        String normalizeUrl = LinksHealthMonitorUtils.normalizeUrl(externalUrl);
        // 本站外部地址不为空时，友链监测记录
        if(StringUtils.isNotBlank(normalizeUrl)) {
            resultSpec.setLinkHealthMonitorRecordList(linkHealthCheck(config, resultSpec, externalUrl));
        }

        // 元数据
        Metadata metadata = new Metadata();
        metadata.setName(LinksHealthMonitorUtils.generateMetadataname());
        monitorResult.setMetadata(metadata);

        // 创建自定义模型的对象
        monitorResult.setResultSpec(resultSpec);

        client.create(monitorResult)
            .doOnError(error -> {
                log.error("{}【{}】创建自定义模型的对象失败: {}", LINKS_HEALTH_MONITOR_DESC, LINKS_HEALTH_MONITOR, error.getMessage(), error);
            })
            .subscribe();
    }

    /**
     * 友链监测
     *
     * @param config 插件配置
     * @param resultSpec 自定义模型的对象
     * @param externalUrl 本站外部地址
     * @return 友链监测记录
     */
    private List<LinksHealthMonitorResult.LinkHealthMonitorRecord> linkHealthCheck(
        LinksHealthMonitorConfig config, LinksHealthMonitorResult.ResultSpec resultSpec, String externalUrl) {

        // 友链监测记录
        List<LinksHealthMonitorResult.LinkHealthMonitorRecord> recordList =
            new ArrayList<>();

        // 查询所有的友链
        List<Link> allLinks = service.getAllLinks();
        // 友链总数
        if (!CollectionUtils.isEmpty(allLinks)) {
            allLinkCount = allLinks.size();
        }

        log.info("{}【{}】友链监测中，友链总数：【{}】", LINKS_HEALTH_MONITOR_DESC, LINKS_HEALTH_MONITOR, allLinkCount);

        // 友链监测
        monitoredLinkCount = 0;
        // 无需监测友链总数
        notRequiredLinkCount = 0;
        for (Link link : allLinks) {
            // 已监测友链总数
            monitoredLinkCount += 1;

            // Link的元数据
            MetadataOperator metadata = link.getMetadata();
            // 获取友链的url
            String metaName = metadata.getName();
            // 友链自定义对象的LinkSpec（Link的表单数据）
            Link.LinkSpec spec = link.getSpec();
            if (StringUtils.isBlank(metaName) || spec == null) {
                continue;
            }

            // 给Link表单增加的元数据
            LinkMetadataAnnotations annotations = fromMap(metadata.getAnnotations());

            // 是否启用友链健康监测
            if (!annotations.isEnableFriendLinkHealthMonitor()) {
                // 无需监测友链总数
                notRequiredLinkCount += 1;
                // 跳出循环
                continue;
            }

            // 监测记录
            LinksHealthMonitorResult.LinkHealthMonitorRecord checkRecord = new LinksHealthMonitorResult.LinkHealthMonitorRecord();

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
            boolean websiteAccessible = LinksHealthMonitorUtils.isUrlAccessible(url);
            checkRecord.setWebsiteAccessible(websiteAccessible);

            if (websiteAccessible) {
                // 网站logo是否可以访问
                if (StringUtils.isNotBlank(logo)) {
                    checkRecord.setLogoAccessible(LinksHealthMonitorUtils.isUrlAccessible(logo));
                } else {
                    checkRecord.setLogoAccessible(false);
                }
                // 网站名称是否有变更
                LinksHealthMonitorUtils.isDisplayNameChanged(url, displayName, checkRecord);
                // 网站是否包含本站友链
                LinksHealthMonitorUtils.isContainsOurLink(LinksHealthMonitorUtils.normalizeUrl(externalUrl), annotations.getFriendLinkUrl(), checkRecord);
            } else {
                // 友链网站不可访问时，后续逻辑不再执行
            }

            recordList.add(checkRecord);
        }

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

        return DEFAULT_CRON;
    }
}
