package cool.tch.linkshealthmonitor.task;

import cool.tch.linkshealthmonitor.config.LinksHealthMonitorConfig;
import cool.tch.linkshealthmonitor.constant.LinksHealthMonitorConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.halo.app.extension.Metadata;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.plugin.ReactiveSettingFetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

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
public class LinksHealthMonitorTaskScheduler {

    // 获取插件配置
    private final ReactiveSettingFetcher reactiveSettingFetcher;

    private ScheduledFuture<?> scheduledFuture;

    // 与自定义模型交互
    private final ReactiveExtensionClient client;

    /**
     * 获取插件配置
     */
    public void getPluginConfig() {
        reactiveSettingFetcher
            .fetch(LinksHealthMonitorConfig.GROUP, LinksHealthMonitorConfig.class)
            .subscribe(this::executetask, error -> {
                // 配置获取失败
                log.error("{}【{}】配置获取失败: {}", LINKS_HEALTH_MONITOR_DESC, LINKS_HEALTH_MONITOR, error.getMessage());
            });
    }

    /**
     * 执行任务
     * @param config 插件的配置
     */
    public void executetask(LinksHealthMonitorConfig config) {
        // 自定义模型的对象
        LinksHealthMonitorResult monitorResult = new LinksHealthMonitorResult();
        LinksHealthMonitorResult.ResultSpec resultSpec = new LinksHealthMonitorResult.ResultSpec();
        // 是否自定义Cron
        resultSpec.setCustomizedCronEnable(config.isCustomizedCronEnable());
        // 自定义cron
        String customizedCron = config.getCustomizedCron();
        resultSpec.setCustomizedCron(customizedCron);

        // 启用自定义cron时,检查自定义cron表达式是否可用
        if (config.isCustomizedCronEnable() && LinksHealthMonitorUtil.checkCronExpression(customizedCron)) {
            // 自定义Cron是否可用
            resultSpec.setCustomizedCronAvailable(true);
            // 实际执行任务的cron
            resultSpec.setPractivalCron(customizedCron);
        } else {
            // 自定义Cron是否可用
            resultSpec.setCustomizedCronAvailable(false);
            // 实际执行任务的cron(默认)
            resultSpec.setPractivalCron(LinksHealthMonitorConstant.DEFAULT_CRON);
        }

        // 实际执行任务的cron
        String practivalCron = resultSpec.getPractivalCron();


        // 如果有任务，先取消
        if (scheduledFuture != null) {
            // 配置变更以后现有的任务已经不需要执行了,直接中断
            scheduledFuture.cancel(true);
        }

        // 执行任务的时间
        resultSpec.setMonitorDate(LinksHealthMonitorUtil.getCurrentDateTime());

        // 友链检测记录
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
        resultSpec.setLinkHealthCheckRecordList(recordList);

        // 元数据
        Metadata metadata = new Metadata();
        metadata.setName(LinksHealthMonitorUtil.generateMetadataname());
        monitorResult.setMetadata(metadata);

        // 创建自定义模型的对象
        monitorResult.setResultSpec(resultSpec);
        Mono<LinksHealthMonitorResult> linksHealthMonitorResultMono = client.create(monitorResult);

        linksHealthMonitorResultMono.subscribe(
          result -> {
              System.out.println("成功成功成功成功成功成功成功成功成功成功 = " + result);
          },
          error -> {
              error.printStackTrace();
          }
        );
    }
}
