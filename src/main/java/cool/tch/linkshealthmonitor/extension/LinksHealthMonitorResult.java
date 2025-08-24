package cool.tch.linkshealthmonitor.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;
import java.util.List;

/**
 * @Author Denchouka
 * @Date 2025/8/21 20:49
 * @Desc 友链检测结果
 */
@Data
@EqualsAndHashCode(callSuper = true)
@GVK(kind = "Result",
    group = "result.linkshealthmonitor.tch.cool",
    version = "v1alpha1",
    singular = "result",
    plural = "results")
public class LinksHealthMonitorResult extends AbstractExtension {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private ResultSpec resultSpec;

    @Data
    public static class ResultSpec {

        // 是否启用自定义Cron表达式
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        private Boolean customizedCronEnable;

        // 自定义cron
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        private String customizedCron;

        // 自定义Cron是否可用
        @Schema(defaultValue = "true")
        private Boolean customizedCronAvailable;

        // 实际执行任务的cron
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        private String practicalCron;

        // 执行任务的时间
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        private String monitorDateTime;

        // 执行任务的时长（单位：毫秒）
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        private String monitorDuration;

        // 友链监测记录
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        private List<LinkHealthCheckRecord> LinkHealthCheckRecordList;
    }

    /**
     * 友链监测记录
     */
    @Data
    public static class LinkHealthCheckRecord {

        // 检索友链的key(自定义模型Link元数据的name)
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        private String linkName;

        // 友链的url(自定义模型的url)
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        private String linkUrl;

        // 友链的网站名称(自定义模型的displayName)
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        private String linkDisplayName;

        // 友链的网站logo(自定义模型的logo)
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        private String linkLogo;

    }
}
