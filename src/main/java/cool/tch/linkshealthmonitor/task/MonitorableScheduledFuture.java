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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;

import static cool.tch.linkshealthmonitor.constant.LinksHealthMonitorConstant.LINKS_HEALTH_MONITOR;
import static cool.tch.linkshealthmonitor.constant.LinksHealthMonitorConstant.LINKS_HEALTH_MONITOR_DESC;

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

    // 上次任务的开始时间
    private final AtomicReference<LocalDateTime> lastStartTime = new AtomicReference<>();

    // 下次任务的执行时间
    private final AtomicReference<LocalDateTime> nextExecutionTime = new AtomicReference<>();

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
        CronTrigger trigger = new CronTrigger(cronExpression);

        // 包装原始任务，添加监控逻辑
        Runnable monitorableTask = () -> {
            // 任务的开始时间
            lastStartTime.set(LocalDateTime.now());
            // 状态为运行中
            status.set(TaskStatus.RUNNING);

            try{
                // 执行任务
                task.run();
            } catch (Exception e){
                // 状态为失败
                status.set(TaskStatus.FAILED);
                log.error("{}【{}】执行任务失败: {}", LINKS_HEALTH_MONITOR_DESC, LINKS_HEALTH_MONITOR, e.getMessage(), e);
            } finally {
                // 更新下次任务的执行时间
                updateNextExecutionTime(trigger);
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
     * 任务是否正在运行（可对外提供）
     * @return 任务是否正在运行
     */
    public boolean isRunning() {
        return status.get() == TaskStatus.RUNNING &&
            scheduledFuture != null &&
            !scheduledFuture.isCancelled() &&
            !scheduledFuture.isDone();
    }

    /**
     * 获取任务状态信息
     * @return 任务状态信息
     */
    public TaskInfo getTaskInfo() {
        return new TaskInfo(
            status.get(),
            cronExpression,
            lastStartTime.get(),
            nextExecutionTime.get(),
            getRemainingTime(),
            isRunning()
        );
    }

    /**
     * 更新下次任务的执行时间
     * @param trigger Cron
     */
    private void updateNextExecutionTime(CronTrigger trigger) {
        TriggerContext context = new SimpleTriggerContext();
        Instant nextExecution = trigger.nextExecution(context);
        if (nextExecution != null) {
            nextExecutionTime.set(LocalDateTime.ofInstant(nextExecution, ZoneId.systemDefault()));
        }
    }

    /**
     * 获取距离下次任务执行的剩余时间（毫秒）
     * @return 距离下次任务执行的剩余时间（毫秒）
     */
    private Long getRemainingTime() {
        LocalDateTime nextTime = nextExecutionTime.get();
        if (nextTime != null) {
            return Duration.between(LocalDateTime.now(), nextTime).toMinutes();
        }

        return null;
    }

    /**
     * 任务状态枚举
     */
    public enum TaskStatus {
        // 已创建
        CREATED,
        // 运行中
        RUNNING,
        // 已暂停
        PAUSED,
        // 已停止
        STOPPED,
        // 执行失败
        FAILED,
        // 任务成功完成
        COMPLETED
    }

    /**
     * 任务信息
     */
    @AllArgsConstructor
    @Getter
    public static class TaskInfo {
        // 任务状态
        private final TaskStatus taskStatus;
        // cron表达式
        private final String cronExpression;
        // 上次任务的开始时间
        private final LocalDateTime lastStartTime;
        // 下次任务的执行时间
        private final LocalDateTime nextExecutionTime;
        // 距离下次任务执行的剩余时间（毫秒）
        private final Long remainingTimeMillis;
        // 任务是否在执行
        private final boolean running;
    }
}
