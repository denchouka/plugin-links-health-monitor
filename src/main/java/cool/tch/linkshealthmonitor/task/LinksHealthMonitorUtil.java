package cool.tch.linkshealthmonitor.task;

import cool.tch.linkshealthmonitor.extension.LinksHealthMonitorResult;
import org.springframework.scheduling.support.CronExpression;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static cool.tch.linkshealthmonitor.constant.LinksHealthMonitorConstant.CUSTOM_MODEL_METADATA_NAME_PREFIX;

/**
 * @Author Denchouka
 * @Date 2025/8/21 21:41
 * @Desc 友链监测的一些功能方法
 */
public class LinksHealthMonitorUtil {

    /**
     * 检查cron表达式
     * @param cronExpression
     * @return
     */
    public static boolean checkCronExpression(String cronExpression) {
        return CronExpression.isValidExpression(cronExpression);
    }

    /**
     * 随机生成自定义模型元数据的名称
     * @return 元数据的名称
     */
    public static String generateMetadataname() {
        return CUSTOM_MODEL_METADATA_NAME_PREFIX + UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取带时区信息的当前时间
     * @return 带时区信息的当前时间
     */
    public static String getCurrentDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime now = ZonedDateTime.now();
        return now.format(formatter);
    }

    /**
     * 监测网站是否可以打开
     * @param url 网站url
     * @param checkRecord 监测记录
     */
    public static void isWebsiteAccessible(String url, LinksHealthMonitorResult.LinkHealthCheckRecord checkRecord) {
    }

    /**
     * 监测网站logo是否可以访问
     * @param url 网站url
     * @param logo 网站logo
     * @param checkRecord 监测记录
     */
    public static void isLogoAccessible(String url, String logo, LinksHealthMonitorResult.LinkHealthCheckRecord checkRecord) {
    }

    /**
     * 监测网站logo是否有变更
     * @param url 网站url
     * @param logo 网站logo
     * @param checkRecord 监测记录
     */
    public static void isLogoChanged(String url, String logo, LinksHealthMonitorResult.LinkHealthCheckRecord checkRecord) {
    }

    /**
     * 网站名称是否有变更
     * @param url 网站url
     * @param displayName 网站名称
     * @param checkRecord 监测记录
     */
    public static void isDisplayNameChanged(String url, String displayName, LinksHealthMonitorResult.LinkHealthCheckRecord checkRecord) {
    }

    /**
     * 监测是否包含本站友链
     * @param url 网站url
     * @param checkRecord 监测记录
     */
    public static void isContainsOurLink(String url, LinksHealthMonitorResult.LinkHealthCheckRecord checkRecord) {
    }

    /**
     * 最新更新文章名称，url，更新时间
     * @param url 网站url
     * @param checkRecord 监测记录
     */
    public static void getLatestArticle(String url, LinksHealthMonitorResult.LinkHealthCheckRecord checkRecord) {
    }
}
