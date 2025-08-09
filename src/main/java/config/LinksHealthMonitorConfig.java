package config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.ReactiveSettingFetcher;

@Component
@RequiredArgsConstructor
public class LinksHealthMonitorConfig {

    private final ReactiveSettingFetcher reactiveSettingFetcher;

    public Mono<BasicConfig> getBasicConfig() {
        return reactiveSettingFetcher.fetch(BasicConfig.GROUP, BasicConfig.class)
            .defaultIfEmpty(new BasicConfig());
    }

    /**
     * 基本设置
     */
    @Data
    class BasicConfig {

        public static final String GROUP = "basic";

        // 是否自定义Cron
        private boolean isCustomizedCron;

        // 自定义cron
        private String customizedCron;

        // 无需监测友链
        private String notRequierdMonitorlinks;
    }
}
