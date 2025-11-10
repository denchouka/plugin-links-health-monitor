package cool.tch.linkshealthmonitor.endpoint;

import cool.tch.linkshealthmonitor.extension.LinksHealthMonitorResult;
import cool.tch.linkshealthmonitor.service.CustomResourceService;
import cool.tch.linkshealthmonitor.task.LinksHealthMonitorTask;
import cool.tch.linkshealthmonitor.task.MonitorableScheduledFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.endpoint.CustomEndpoint;
import run.halo.app.extension.GroupVersion;

import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * @Author Denchouka
 * @Date 2025/10/2 11:15
 * @Desc 插件自定义api
 */
@Component
@RequiredArgsConstructor
public class LinksHealthMonitorEndpoint implements CustomEndpoint {

    private final LinksHealthMonitorTask linksHealthMonitorTask;

    // 操作自定义模型
    private final CustomResourceService service;

    @Override
    public RouterFunction<ServerResponse> endpoint() {
        return RouterFunctions.route()
            .GET("/status", RequestPredicates.accept(APPLICATION_JSON), this::status)
            .GET("/latestResult", RequestPredicates.accept(APPLICATION_JSON), this::latestResults)
            .build();
    }

    /**
     * 获取任务执行信息
     * @param request request
     * @return 任务执行信息
     */
    private Mono<ServerResponse> status(ServerRequest request) {
        MonitorableScheduledFuture.TaskInfo taskInfo =
            linksHealthMonitorTask.getTaskExecuteInfo();
        return ServerResponse.ok().bodyValue(taskInfo);
    }

    /**
     * 获取最新友链监测记录
     * @param request request
     * @return 友链监测结果
     */
    private Mono<ServerResponse> latestResults(ServerRequest request) {
        Mono<LinksHealthMonitorResult> latestResult = service.getLatestResult();
        return ServerResponse.ok().body(latestResult, LinksHealthMonitorResult.class);
    }

    @Override
    public GroupVersion groupVersion() {
        return new GroupVersion("result.linkshealthmonitor.tch.cool", "v1alpha1");
    }
}