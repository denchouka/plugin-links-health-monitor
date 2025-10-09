package cool.tch.linkshealthmonitor.endpoint;

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

    @Override
    public RouterFunction<ServerResponse> endpoint() {
        return RouterFunctions.route()
            .GET("/status", RequestPredicates.accept(APPLICATION_JSON), this::status)
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
        System.out.println("任务信息 = " + taskInfo.toString());
        return ServerResponse.ok().bodyValue(taskInfo);
    }

    @Override
    public GroupVersion groupVersion() {
        return new GroupVersion("result.linkshealthmonitor.tch.cool", "v1alpha1");
    }
}