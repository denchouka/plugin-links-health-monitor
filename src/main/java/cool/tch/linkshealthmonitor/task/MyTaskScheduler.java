package cool.tch.linkshealthmonitor.task;

import cool.tch.linkshealthmonitor.constant.Constant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @Author Denchouka
 * @Date 2025/8/23 16:53
 * @Desc 自定义一个TaskScheduler的Bean
 */
@Configuration
public class MyTaskScheduler {

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(Constant.CUSTOM_THREAD_POOL_SIZE);
        scheduler.setThreadNamePrefix(Constant.CUSTOM_MODEL_METADATA_NAME_PREFIX);
        // 服务关闭时,不等待正在执行的任务完成
        scheduler.setWaitForTasksToCompleteOnShutdown(false);
        //取消的任务立即从队列中移除，避免资源占用
        scheduler.setRemoveOnCancelPolicy(true);
        scheduler.initialize();
        return scheduler;
    }
}
