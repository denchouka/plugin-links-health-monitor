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

    // 自定义模型元数据名称前缀
    public static final String CUSTOM_MODEL_METADATA_NAME_PREFIX = "links-health-monitor-";

    // 自定义线程池线程数
    // 最多只会有2个线程，一个执行旧的配置对应的任务，一个执行新的配置对应的任务，一个备用（极端情况下，单个任务的执行时间超过任务的执行周期）
    public static final int CUSTOM_THREAD_POOL_SIZE = 3;

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
}
