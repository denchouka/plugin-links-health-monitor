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
 * @Desc 友链监测结果
 */
@Data
@EqualsAndHashCode(callSuper = true)
@GVK(kind = "MonitorResult",
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
        private String customizedCron;

        // 自定义Cron是否可用
        @Schema(defaultValue = "true")
        private Boolean customizedCronAvailable;

        // 友链监测记录
        private List<LinkHealthMonitorRecord> LinkHealthMonitorRecordList;

        // 本站外部地址
        private String externalUrl;

        // 友链总数
        private Integer linkCount;

        // 无需监测友链总数
        private Integer notRequiredLinkCount;
    }

    /**
     * 友链监测记录
     */
    @Data
    public static class LinkHealthMonitorRecord {

        // 友链基本信息
        // 检索友链的key(自定义模型Link元数据的name)
        private String linkName;

        // 友链的url(自定义模型的url)
        private String linkUrl;

        // 友链的网站名称(自定义模型的displayName)
        private String linkDisplayName;

        // 友链的网站logo(自定义模型的logo)
        private String linkLogo;

        // 友链的网站分组(自定义模型的displayName)
        private String linkGroup;

        // 友链的网站分组(自定义模型的groupName对应的displayName)
        private String linkGroupDisplayName;

        // 功能监测
        // 网站是否可以访问
        private boolean websiteAccessible;

        // 网站logo是否可以访问
        private boolean logoAccessible;

        // 网站名称是否有变更 (displayName)
        private boolean displayNameChanged;

        // 网站的最新名称（有变更的情况下）
        private String latestDisplayName;

        // 是否可以获取友链页面路由
        private boolean getFriendLinkRoute;

        // 是否包含本站友链
        private boolean containsOurLink;
    }
}
