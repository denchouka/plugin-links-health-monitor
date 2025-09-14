package cool.tch.linkshealthmonitor.task;

import cool.tch.linkshealthmonitor.config.LinksHealthMonitorConfig;
import cool.tch.linkshealthmonitor.extension.LinksHealthMonitorResult;
import org.springframework.scheduling.support.CronExpression;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

import static cool.tch.linkshealthmonitor.constant.Constant.CUSTOM_MODEL_METADATA_NAME_PREFIX;
import static cool.tch.linkshealthmonitor.constant.Constant.DEFAULT_FRIEND_LINK_ROUTES;
import static cool.tch.linkshealthmonitor.constant.Constant.HTTP_REQUEST_METHOD_GET;
import static cool.tch.linkshealthmonitor.constant.Constant.HTTP_TIMEOUT_MS;
import static cool.tch.linkshealthmonitor.constant.Constant.LOCAL_DATE_TIME_OUTPUT_FORMATTER;

/**
 * @Author Denchouka
 * @Date 2025/8/21 21:41
 * @Desc 友链监测的一些功能方法
 */
public class LinksHealthMonitorUtils {

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
     * 获取全部的友链页面路由
     * @param config 插件配置
     * @return 全部的友链页面路由，默认路由在前
     */
    public static String[] getAllFriendLinkRoutes(LinksHealthMonitorConfig config) {
        // 插件设置页面添加的路由
        String[] friendLinkRoutes = config.getFriendLinkRoutes();

        Stream<String> friendLinkRoutesStream = (friendLinkRoutes == null || friendLinkRoutes.length == 0) ? Stream.empty()
            : Arrays.stream(friendLinkRoutes)
                .map(LinksHealthMonitorUtils::routeInitProcessing)
                // 初始化处理后先去重
                .distinct()
                .filter(LinksHealthMonitorUtils::isValidAndNonRootRoute)
                .map(LinksHealthMonitorUtils::normalizeRoute)
                // 再次判空（防御）
                .filter(route -> !route.isEmpty())
                // 去重
                .distinct();

        // 默认的友链页面路由
        Stream<String> defaultFriendLinkRoutesStream = Arrays.stream(DEFAULT_FRIEND_LINK_ROUTES);

        // 默认的友链页面路由在前
        return Stream.concat(defaultFriendLinkRoutesStream, friendLinkRoutesStream)
            // 结果继续去重
            .distinct()
            .toArray(String[]::new);
    }

    /**
     * 初始化处理页面路由
     * @param route 页面路由
     * @return 初始化处理后的页面路由
     */
    private static String routeInitProcessing(String route) {
        // null -> ""
        if (route == null ) route = "";

        // 去空格
        route = route.trim();

        // 先把所有的"\"转换成"/"，后续所有逻辑都基于"/"处理
        route = route.replace("\\", "/");

        // 连续多个"/"替换成一个
        route = route.replaceAll("/+", "/");

        return route;
    }

    /**
     * 判断是否为有效且非根路径的路由
     * @param route 页面路由
     * @return 路由是否可用
     */
    private static boolean isValidAndNonRootRoute(String route) {
        // 是否为空
        if (route.isEmpty()) return false;

        // 是否为根路径（包括 "///", " / ", 等变体）
        if (route.chars().allMatch(ch -> ch == '/' || Character.isWhitespace(ch))) {
            return false;
        }

        return true;
    }

    /**
     * 标准化路由格式
     * @param route 页面路由
     * @return 标准化后的页面路由
     */
    private static String normalizeRoute(String route) {
        // 必须以"/"开头
        if (!route.startsWith("/")) {
            route = "/" + route;
        }

        // 不能以"/"结尾
        if (route.length() > 1 && route.endsWith("/")) {
            route = route.substring(0, route.length() - 1);
        }

        return route;
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
