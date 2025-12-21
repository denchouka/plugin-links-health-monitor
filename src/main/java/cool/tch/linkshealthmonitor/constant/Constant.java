package cool.tch.linkshealthmonitor.constant;

/**
 * @Author Denchouka
 * @Date 2025/8/10 22:29
 * @Desc 常量
 */
public class Constant {

    public static final String LINKS_HEALTH_MONITOR = "Links Health Monitor";

    public static final String LINKS_HEALTH_MONITOR_DESC = "友情链接健康监测插件";

    // 默认的cron表达式：每天0点执行一次
    public static final String DEFAULT_CRON = "0 0 0 * * ?";

    // 默认的cron表达式描述
    public static final String DEFAULT_CRON_DESC = "（默认的Cron表达式，每天0点执行监测一次）";

    // 自定义模型元数据名称前缀
    public static final String CUSTOM_MODEL_METADATA_NAME_PREFIX = "links-health-monitor-";

    // 自定义线程池线程数
    // 有新的任务时会先停止旧的任务，同一时间最多只会有一个任务（2：冗余配置，防止意外情况）
    public static final int CUSTOM_THREAD_POOL_SIZE = 2;

    // 服务关闭时,等待所有正在执行的任务完成，最多等待的秒数
    public static final int CUSTOM_THREAD_AWAIT_TIME = 60;

    // 默认的最短时间，供前端展示
    public static final String DEFAULT_SHORTEST_TIME = "1秒";

    // 下次任务执行时间已过
    public static final String NEXT_TASK_TIME_PAST = "时间已过";

    // 默认的时区（明确指定为北京时间）
    public static final String DEFAULT_ZONE_ID = "Asia/Shanghai";

    // LocalDateTime类型数据输出的格式
    public static final String LOCAL_DATE_TIME_OUTPUT_FORMATTER = "yyyy年MM月dd日 HH时mm分ss秒";

    // 网络连接超时（毫秒）
    public static final int HTTP_TIMEOUT_MS = 10000;

    // 网络请求方法 GET
    public static final String HTTP_REQUEST_METHOD_GET = "GET";

    // 网络请求User-Agent
    public static final String HTTP_REQUEST_USER_AGENT = "Mozilla/5.0 (compatible; FriendLinkHealth-Monitor/1.0)";

    // 默认的友链页面路由
    public static final String[] DEFAULT_FRIEND_LINK_ROUTES = {"/links", "/link", "/friends"};

    // 路径分隔符
    public static final String PATH_SEPARATOR = "/";
}
