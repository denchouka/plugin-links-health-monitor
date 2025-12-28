package cool.tch.linkshealthmonitor.task;

import cool.tch.linkshealthmonitor.config.LinksHealthMonitorConfig;
import cool.tch.linkshealthmonitor.extension.LinksHealthMonitorResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.support.CronExpression;
import java.net.HttpURLConnection;
import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

import static cool.tch.linkshealthmonitor.constant.Constant.CUSTOM_MODEL_METADATA_NAME_PREFIX;
import static cool.tch.linkshealthmonitor.constant.Constant.DEFAULT_FRIEND_LINK_ROUTES;
import static cool.tch.linkshealthmonitor.constant.Constant.HTTP_REQUEST_METHOD_GET;
import static cool.tch.linkshealthmonitor.constant.Constant.HTTP_REQUEST_USER_AGENT;
import static cool.tch.linkshealthmonitor.constant.Constant.HTTP_TIMEOUT_MS;
import static cool.tch.linkshealthmonitor.constant.Constant.LOCAL_DATE_TIME_OUTPUT_FORMATTER;
import static cool.tch.linkshealthmonitor.constant.Constant.PATH_SEPARATOR;

/**
 * @Author Denchouka
 * @Date 2025/8/21 21:41
 * @Desc 友链监测的一些功能方法
 */
@Slf4j
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
        if (StringUtils.isBlank(route)) route = "";

        // 去空格
        route = route.trim();

        // 先把所有的"\"转换成"/"，后续所有逻辑都基于"/"处理
        route = route.replace("\\", PATH_SEPARATOR);

        // 连续多个"/"替换成一个
        route = route.replaceAll("/+", PATH_SEPARATOR);

        return route;
    }

    /**
     * 判断是否为有效且非根路径的路由
     * @param route 页面路由
     * @return 路由是否可用
     */
    private static boolean isValidAndNonRootRoute(String route) {
        // 是否为空
        if (StringUtils.isBlank(route)) return false;

        // 是否为根路径（包括 "///", " / ", 等变体）
        // isWhitespace能识别空白字符，包括空格、制表符('\t')、换行符('\n')、回车('\r')等等
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
        if (!route.startsWith(PATH_SEPARATOR)) {
            route = PATH_SEPARATOR + route;
        }

        // 不能以"/"结尾
        if (route.length() > 1 && route.endsWith(PATH_SEPARATOR)) {
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
            HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
            connection.setRequestMethod(HTTP_REQUEST_METHOD_GET);
            connection.setConnectTimeout(HTTP_TIMEOUT_MS);
            connection.setReadTimeout(HTTP_TIMEOUT_MS);
            connection.setRequestProperty("User-Agent", HTTP_REQUEST_USER_AGENT);
            return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 监测网站名称是否有变更
     *
     * @param url 网站url
     * @param displayName 网站名称
     * @param checkRecord 监测记录
     */
    public static void isDisplayNameChanged(String url, String displayName, LinksHealthMonitorResult.LinkHealthMonitorRecord checkRecord) {

        try{
            Document document = Jsoup
                .connect(url)
                .timeout(HTTP_TIMEOUT_MS)
                .userAgent(HTTP_REQUEST_USER_AGENT)
                .get();
            String title = document.title();
            checkRecord.setDisplayNameChanged(title.contains(displayName));
            checkRecord.setLatestDisplayName(title);
        } catch(Exception e) {
            checkRecord.setDisplayNameChanged(false);
        }

    }

    /**
     * 监测网站是否包含本站友链
     *
     * @param url 网站url
     * @param ourUrl 本站外部访问地址
     * @param allFriendLinkRoutes 全部的友链页面路由
     * @param checkRecord 监测记录
     */
    public static void isContainsOurLink(String url, String ourUrl, String[] allFriendLinkRoutes, LinksHealthMonitorResult.LinkHealthMonitorRecord checkRecord) {
        // 获取本站外部地址失败
        if (StringUtils.isBlank(ourUrl)) {
            return;
        }

        // 是否可以获取友链页面路由（默认不可以）
        boolean getFriendLinkRoute = false;
        // 完整的友链页面url
        String friendLinkUrl = null;

        // 遍历全部的友链页面路由
        for (String route : allFriendLinkRoutes) {
            // 拼接完整url，并检查是否可以访问
            String wholeUrl = StringUtils.join(url, route);
            if (isUrlAccessible(wholeUrl)) {
                getFriendLinkRoute = true;
                friendLinkUrl = wholeUrl;
                break;
            }
        }

        // 获取不到友链页面路由时直接结束
        if (!getFriendLinkRoute) {
            return;
        }

        // 友链页面路由
        checkRecord.setFriendLinkRoute(friendLinkUrl);

        // 是否包含本站友链
        try{
            Document document = Jsoup
                .connect(friendLinkUrl)
                .timeout(HTTP_TIMEOUT_MS)
                .userAgent(HTTP_REQUEST_USER_AGENT)
                .get();

            // <a>标签
            Elements hrefLinks = document.select("a[href]");
            for (Element link : hrefLinks) {
                if (link.attr("abs:href").equals(ourUrl)) {
                    checkRecord.setContainsOurLink(true);
                    break;
                }
            }

            // <input>标签
            if (!checkRecord.isContainsOurLink()) {
                Elements selectLinks = document.select("input[type=submit]");
                for (Element link : selectLinks) {
                    if (link.val().equals(ourUrl)) {
                        checkRecord.setContainsOurLink(true);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            // 访问友链页面失败
        }
    }

    /**
     * 标准化url
     * @param url
     * @return
     */
    public static String normalizeUrl(String url) {

        // 空
        if (StringUtils.isBlank(url)) {
            return url;
        }

        // 把所有的"\"转换成"/"，后续所有逻辑都基于"/"处理
        url = url.replace("\\", PATH_SEPARATOR);

        // 连续多个"/"替换成一个
        url = url.replaceAll("/+", PATH_SEPARATOR);

        // https:/ -> https://
        url = url.replace("https:/", "https://");

        // 删除末尾"/"
        if (url.length() > 1 && url.endsWith(PATH_SEPARATOR)) {
            url = url.substring(0, url.length() - 1);
        }

        // 如果是根路径"/"时转成""，后续会有空的判断
        if (PATH_SEPARATOR.equals(url)) {
            url = "";
        }

        return url;
    }

    /**
     * 获取友链监测进度
     * @param allLinkCount 友链总数
     * @param notRequiredLinkCount 无需监测友链总数
     * @param monitoredLinkCount 已监测友链总数
     * @return
     */
    public static String getLinkMonitorProgress(int allLinkCount, int notRequiredLinkCount, int monitoredLinkCount) {
        return monitoredLinkCount + "/" + allLinkCount + "（无需监测友链数" + notRequiredLinkCount + "）";
    }
}
