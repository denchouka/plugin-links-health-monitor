package cool.tch.linkshealthmonitor.task;

import cool.tch.linkshealthmonitor.config.LinksHealthMonitorConfig;
import cool.tch.linkshealthmonitor.constant.LinksHealthMonitorConstant;
import cool.tch.linkshealthmonitor.extension.Link;
import cool.tch.linkshealthmonitor.extension.LinksHealthMonitorResult;
import cool.tch.linkshealthmonitor.service.CustomResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import run.halo.app.extension.Metadata;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.plugin.ReactiveSettingFetcher;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static cool.tch.linkshealthmonitor.constant.LinksHealthMonitorConstant.LINKS_HEALTH_MONITOR;
import static cool.tch.linkshealthmonitor.constant.LinksHealthMonitorConstant.LINKS_HEALTH_MONITOR_DESC;

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
            "0 0/5 * * * ?"
        );

        // 启动任务
        scheduledFuture.start();
    }

    /**
     * 执行任务逻辑
     * @param config 插件配置
     */
    private void executeTaskLogic(LinksHealthMonitorConfig config) {
        // 获取任务信息
        MonitorableScheduledFuture.TaskInfo info = scheduledFuture.getTaskInfo();
        System.out.println("获取任务信息获取任务信息获取任务信息获取任务信息获取任务信息获取任务信息获取任务信息获取任务信息");
        System.out.println("任务状态: " + info.getTaskStatus());
        System.out.println("Cron表达式: " + info.getCronExpression());
        System.out.println("上次执行时间: " + info.getLastStartTime());
        System.out.println("下次执行时间: " + info.getNextExecutionTime());
        System.out.println("剩余时间(ms): " + info.getRemainingTimeMillis());
        System.out.println("自定义cron: " + config.getCustomizedCron());
        System.out.println("获取任务信息获取任务信息获取任务信息获取任务信息获取任务信息获取任务信息获取任务信息获取任务信息");

        // 任务开始
        long start = Instant.now().toEpochMilli();

        // 自定义模型的对象
        LinksHealthMonitorResult monitorResult = new LinksHealthMonitorResult();
        LinksHealthMonitorResult.ResultSpec resultSpec = new LinksHealthMonitorResult.ResultSpec();
        // 是否启用自定义Cron
        resultSpec.setCustomizedCronEnable(config.isCustomizedCronEnable());
        // 自定义cron
        String customizedCron = config.getCustomizedCron();
        resultSpec.setCustomizedCron(customizedCron);

        // 自定义Cron是否可用
        resultSpec.setCustomizedCronAvailable(LinksHealthMonitorUtil.checkCronExpression(customizedCron));
        // 实际执行任务的cron
        String practicalCron = getPractialCron(config);
        resultSpec.setPracticalCron(practicalCron);
        // 执行任务的时间
        resultSpec.setMonitorDateTime(LinksHealthMonitorUtil.getCurrentDateTime());

        // 友链检测记录
        resultSpec.setLinkHealthCheckRecordList(linkHealthCheck(config));

        // 任务结束
        long end = Instant.now().toEpochMilli();
        // 执行任务的时长（单位：秒）
        resultSpec.setMonitorDuration(String.format("%.3f", (end - start) / 1000.0));

        // 元数据
        Metadata metadata = new Metadata();
        metadata.setName(LinksHealthMonitorUtil.generateMetadataname());
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
     * 友链检测
     * @param config 插件配置
     * @return 友链检测记录
     */
    private List<LinksHealthMonitorResult.LinkHealthCheckRecord> linkHealthCheck(
        LinksHealthMonitorConfig config) {

        // 查询所有的友链
        List<Link> allLinks = service.getAllLinks();
        allLinks.forEach(link -> {
            System.out.println("友链数据友链数据友链数据友链数据 = " + link.toString());
        });

        List<LinksHealthMonitorResult.LinkHealthCheckRecord> recordList =
            new ArrayList<>();
        // 假的检测记录
        LinksHealthMonitorResult.LinkHealthCheckRecord checkRecord =
            new LinksHealthMonitorResult.LinkHealthCheckRecord();
        checkRecord.setLinkName("test-link");
        checkRecord.setLinkUrl("http://localhost:8080");
        checkRecord.setLinkDisplayName("test-link-name");
        checkRecord.setLinkLogo("http://localhost:8080/logo.png");
        recordList.add(checkRecord);

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
        if (config.isCustomizedCronEnable() && LinksHealthMonitorUtil.checkCronExpression(customizedCron)) {
           return customizedCron;
        }

        return LinksHealthMonitorConstant.DEFAULT_CRON;
    }
}
