package cool.tch.linkshealthmonitor.task;

import cool.tch.linkshealthmonitor.extension.LinksHealthMonitorResult;
import org.springframework.scheduling.support.CronExpression;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static cool.tch.linkshealthmonitor.constant.Constant.CUSTOM_MODEL_METADATA_NAME_PREFIX;
import static cool.tch.linkshealthmonitor.constant.Constant.HTTP_REQUEST_METHOD_GET;
import static cool.tch.linkshealthmonitor.constant.Constant.HTTP_TIMEOUT_MS;
import static cool.tch.linkshealthmonitor.constant.Constant.LOCAL_DATE_TIME_OUTPUT_FORMATTER;

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_OUTPUT_FORMATTER);
        ZonedDateTime now = ZonedDateTime.now();
        return now.format(formatter);
    }

    /**
     * 监测url是否可以访问
     * @param url url
     * @return url是否可以访问
     */
    public static boolean isUrlAccessible(String url) {

        try{
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod(HTTP_REQUEST_METHOD_GET);
            connection.setConnectTimeout(HTTP_TIMEOUT_MS);
            connection.setReadTimeout(HTTP_TIMEOUT_MS);
            return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (Exception error) {
            return false;
        }
    }

    /**
     * 监测网站名称是否有变更
     * @param url 网站url
     * @param displayName 网站名称
     * @return 网站名称是否有变更
     */
    public static boolean isDisplayNameChanged(String url, String displayName) {
        return false;
    }

    /**
     * 监测网站是否包含本站友链
     * @param url 网站url
     * @param ourUrl 本站外部访问地址
     * @return 网站是否包含本站友链
     */
    public static boolean isContainsOurLink(String url, String ourUrl) {
        // 获取本站外部地址失败
        if (ourUrl == null || ourUrl.isEmpty()) {
            return false;
        }

        return false;
    }

    /**
     * 最新更新文章名称，url，更新时间
     * @param url 网站url
     * @param checkRecord 监测记录
     */
    public static void getLatestArticle(String url, LinksHealthMonitorResult.LinkHealthCheckRecord checkRecord) {
        checkRecord.setLatestArticleTitle("测试文章名称");
        checkRecord.setLatestArticleUrl("测试文章url");
        checkRecord.setLatestArticleTime(getCurrentDateTime());
    }
}
