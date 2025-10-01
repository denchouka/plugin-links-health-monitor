package cool.tch.linkshealthmonitor.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;

import static cool.tch.linkshealthmonitor.constant.Constant.DEFAULT_SHORTEST_TIME;
import static cool.tch.linkshealthmonitor.constant.Constant.DEFAULT_ZONE_ID;
import static cool.tch.linkshealthmonitor.constant.Constant.LINKS_HEALTH_MONITOR;
import static cool.tch.linkshealthmonitor.constant.Constant.LINKS_HEALTH_MONITOR_DESC;
import static cool.tch.linkshealthmonitor.constant.Constant.LOCAL_DATE_TIME_OUTPUT_FORMATTER;
import static cool.tch.linkshealthmonitor.constant.Constant.NEXT_TASK_TIME_PAST;

/**
 * @Author Denchouka
 * @Date 2025/8/24 13:03
 * @Desc 自定义的可监控的定时任务封装
 */
@Slf4j
public class MonitorableScheduledFuture {

    // 可运行的任务
    private final Runnable task;

    // 在某个时间调度任务
    private final TaskScheduler taskScheduler;

    // 用来控制和查询当前正在运行或等待执行的任务（不是final，会在后续被赋值）
    private ScheduledFuture<?> scheduledFuture;

    // cron表达式
    private final String cronExpression;

    // 线程安全的任务状态
    private final AtomicReference<TaskStatus> status = new AtomicReference<>();

    // 任务的计划执行时间（本地时间）
    private final AtomicReference<LocalDateTime> lastScheduledExecution = new AtomicReference<>();

    // 任务的实际执行时间（本地时间）
    private final AtomicReference<LocalDateTime> lastActualExecution = new AtomicReference<>();

    // 任务的实际完成时间（本地时间）
    private final AtomicReference<LocalDateTime> lastCompletionExecution = new AtomicReference<>();

    // 下次任务的计划执行时间（本地时间）
    private final AtomicReference<LocalDateTime> nextScheduledExecution = new AtomicReference<>();

    public MonitorableScheduledFuture(Runnable task, TaskScheduler taskScheduler, String cronExpression) {
        this.task = task;
        this.taskScheduler = taskScheduler;
        this.cronExpression = cronExpression;
    }

    /**
     * 开始定时任务（可对外提供）
     */
    public void start() {

        // 开始新的任务，状态为已创建
        status.set(TaskStatus.CREATED);
        // cron
        CronTrigger trigger = new CronTrigger(cronExpression, getZoneId());

        // 包装原始任务，添加监控逻辑
        Runnable monitorableTask = () -> {

            // 更新任务的计划执行时间
            updateLastScheduledExecution();

            try{
                // 状态为运行中
                status.set(TaskStatus.RUNNING);
                // 更新任务的实际执行时间
                updateLastActualExecution();

                // 执行任务
                task.run();

                // 更新任务的实际完成时间
                updateLastCompletionExecution();
                // 状态为完成
                status.set(TaskStatus.COMPLETED);
            } catch (Exception e){
                // 状态为失败
                status.set(TaskStatus.FAILED);
                log.error("{}【{}】执行任务失败: {}", LINKS_HEALTH_MONITOR_DESC, LINKS_HEALTH_MONITOR, e.getMessage(), e);
            } finally {
                // 更新下次任务的执行时间
                updateNextScheduledExecution(trigger);
                // 任务结束，获取任务执行信息 测试用 TODO
                getTaskExecuteInfo();
            }
        };

        // 执行任务
        scheduledFuture = taskScheduler.schedule(monitorableTask, trigger);
    }

    /**
     * 停止定时任务（可对外提供）
     */
    public void stop() {
        if (scheduledFuture != null) {
            // 配置变更以后停止新的任务执行，现有的任务也不继续执行
            scheduledFuture.cancel(true);
            // 任务状态改为已停止
            status.set(TaskStatus.STOPPED);
        }
    }

    /**
     * 任务是否正在运行
     * @return 任务是否正在运行
     */
    private boolean isRunning() {
        return status.get() == TaskStatus.RUNNING &&
            scheduledFuture != null &&
            !scheduledFuture.isCancelled() &&
            !scheduledFuture.isDone();
    }

    /**
     * 获取任务执行信息（可对外提供）
     * @return 任务执行信息
     */
    public TaskInfo getTaskExecuteInfo() {
        // 获取任务信息 TODO
        TaskInfo info = getTaskInfo();
        System.out.println("获取任务信息获取任务信息获取任务信息获取任务信息获取任务信息获取任务信息获取任务信息获取任务信息----------start");
        System.out.println("任务状态: " + info.getTaskStatus());
        System.out.println("Cron表达式: " + info.getCronExpression());
        System.out.println("任务的计划执行时间: " + info.getLastScheduledExecution());
        System.out.println("任务的实际执行时间: " + info.getLastActualExecution());
        System.out.println("任务的实际完成时间: " + info.getLastCompletionExecution());
        System.out.println("任务的执行时长（天时分秒）: " + info.getLastCompletionTime());
        System.out.println("下次任务的计划执行时间 " + info.getNextScheduledExecution());
        System.out.println("当前距离下次任务执行剩余时间(天时分秒): " + info.getRemainingTime());
        System.out.println("任务是否正在运行: " + info.isRunning());
        System.out.println("获取任务信息获取任务信息获取任务信息获取任务信息获取任务信息获取任务信息获取任务信息获取任务信息----------end");
        return info;
    }

    /**
     * 获取任务状态信息
     * @return 任务状态信息
     */
    private TaskInfo getTaskInfo() {
        return new TaskInfo(
            status.get().getDesc(),
            cronExpression,
            format(lastScheduledExecution),
            format(lastActualExecution),
            format(lastCompletionExecution),
            getLastCompletionTime(),
            format(nextScheduledExecution),
            getRemainingTime(),
            isRunning()
        );
    }

    /**
     * 更新任务的计划执行时间
     */
    private void updateLastScheduledExecution() {
        // 任务的计划执行时间(也是上次预计的下次计划执行时间)
        if (nextScheduledExecution.get() != null) {
            lastScheduledExecution.set(nextScheduledExecution.get());
        } else {
            // 第一次执行则为当前时间
            lastScheduledExecution.set(now());
        }
    }

    /**
     * 更新任务的实际执行时间
     */
    private void updateLastActualExecution() {
        // 任务的实际执行时间就是当前时间
        lastActualExecution.set(now());
    }

    /**
     * 更新任务的实际完成时间
     */
    private void updateLastCompletionExecution() {
        lastCompletionExecution.set(now());
    }

    /**
     * 更新下次任务的执行时间
     * @param trigger Cron
     */
    private void updateNextScheduledExecution(CronTrigger trigger) {
        // 上次计划执行时间
        Instant lastScheduled = localDateTime2Instant(lastScheduledExecution);
        // 上次实际执行时间
        Instant lastActual = localDateTime2Instant(lastActualExecution);
        // 上次完成时间
        Instant lastCompletion = localDateTime2Instant(lastCompletionExecution);

        TriggerContext context = new SimpleTriggerContext(lastScheduled, lastActual, lastCompletion);
        Instant nextExecution = trigger.nextExecution(context);
        if (nextExecution != null) {
            nextScheduledExecution.set(instant2LocalDateTime(nextExecution));
        }
    }

    /**
     * 获取任务的执行时长（天时分秒）
     * @return 任务的执行时长（天时分秒）
     */
    private String getLastCompletionTime() {
        if (lastActualExecution.get() == null || lastCompletionExecution.get() == null) {
            return null;
        }

        // 任务的实际执行时间 和 任务的实际完成时间 的时间间隔
        return getTimeDuration(lastActualExecution.get(), lastCompletionExecution.get());
    }

    /**
     * 获取当前时间距离下次任务执行的剩余时间（天时分秒）
     * @return 当前时间距离下次任务执行的剩余时间（天时分秒）
     */
    private String getRemainingTime() {
        // 任务还未执行完时，nextScheduledExecution
        if (nextScheduledExecution.get() == null) {
            return null;
        }

        return getTimeDuration(now(), nextScheduledExecution.get());
    }

    /**
     * 获取两个时间的时间间隔
     * @param start 开始时间
     * @param end 结束时间
     * @return 时间间隔 （天时分秒）
     */
    private String getTimeDuration(LocalDateTime start, LocalDateTime end) {
        Duration between = Duration.between(start, end);

        // 负数（时间已过）
        if (between.isNegative()) {
            return NEXT_TASK_TIME_PAST;
        }

        long days = between.toDays();
        int hours = between.toHoursPart();
        int minutes = between.toMinutesPart();
        int seconds = between.toSecondsPart();

        // 返回结果的拼接
        StringBuilder timeDuration = new StringBuilder();
        if (days > 0) timeDuration.append(days).append("天");
        if (hours > 0) timeDuration.append(hours).append("时");
        if (minutes > 0) timeDuration.append(minutes).append("分");
        if (seconds > 0) timeDuration.append(seconds).append("秒");

        // 极短的时间（0秒）时单独处理，供前端展示
        if (timeDuration.isEmpty()) {
            return DEFAULT_SHORTEST_TIME;
        } else {
            return timeDuration.toString();

        }
    }

    /**
     * 返回统一的时区
     * @return 时区
     */
    private ZoneId getZoneId() {
        // 强制使用中国标准时区
        return ZoneId.of(DEFAULT_ZONE_ID);
    }

    /**
     * 返回本地当前时间（LocalDateTime）
     * @return 本地当前时间（基于北京时间）
     */
    private LocalDateTime now() {
        return LocalDateTime.now(getZoneId());
    }

    /**
     * LocalDateTime类型转换为Instant
     * @param localDateTime LocalDateTime类型数据
     * @return Instant类型数据
     */
    private Instant localDateTime2Instant(AtomicReference<LocalDateTime> localDateTime) {
        if (localDateTime.get() == null) {
            return null;
        }

        return localDateTime.get().atZone(getZoneId()).toInstant();
    }

    /**
     * Instant类型转换为LocalDateTime
     * @param instant Instant类型数据
     * @return LocalDateTime类型数据
     */
    private LocalDateTime instant2LocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, getZoneId());
    }

    /**
     * LocalDateTime类型数据格式化
     * @param localDateTime LocalDateTime类型数据
     * @return 格式化后的结果
     */
    private String format(AtomicReference<LocalDateTime> localDateTime) {
        if (localDateTime.get() == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_OUTPUT_FORMATTER);
        return localDateTime.get().format(formatter);
    }

    /**
     * 任务状态枚举
     */
    private enum TaskStatus {
        // 已创建
        CREATED("已创建,等待执行"),
        // 运行中
        RUNNING("执行中"),
        // 已停止
        STOPPED("已停止"),
        // 执行失败
        FAILED("执行失败"),
        // 任务成功
        COMPLETED("任务成功");

        // 任务状态
        private final String desc;

        TaskStatus(String desc) {
            this.desc = desc;
        }

        // 获取任务状态
        private String getDesc() {
            return desc;
        }
    }

    /**
     * 任务信息
     */
    @AllArgsConstructor
    @Getter
    private static class TaskInfo {
        // 任务状态（前端展示）
        private final String taskStatus;
        // cron表达式（前端展示）
        private final String cronExpression;
        // 任务的计划执行时间
        private final String lastScheduledExecution;
        // 任务的实际执行时间（前端展示）
        private final String lastActualExecution;
        // 任务的实际完成时间（前端展示）
        private final String lastCompletionExecution;
        // 任务的执行时长（天时分秒）（前端展示）
        private final String lastCompletionTime;
        // 下次任务的计划执行时间（前端展示）
        private final String nextScheduledExecution;
        // 距离下次任务执行的剩余时间（天时分秒）（前端展示）
        private final String remainingTime;
        // 任务是否在执行（前端展示） --TODO 准备删除
        private final boolean running;
    }
}
