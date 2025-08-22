package cool.tch.linkshealthmonitor.task;

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
        return false;
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

}
