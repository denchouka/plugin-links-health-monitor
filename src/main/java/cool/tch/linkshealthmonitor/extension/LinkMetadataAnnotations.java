package cool.tch.linkshealthmonitor.extension;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

import static cool.tch.linkshealthmonitor.constant.Constant.ANNOTATIONS_FIELD_ENABLE_MONITOR;
import static cool.tch.linkshealthmonitor.constant.Constant.ANNOTATIONS_FIELD_FRIEND_LINK_URL;

/**
 * @Author Denchouka
 * @Date 2026/1/4 18:19
 * @Desc Link表达增加的元数据
 */
@Data
@AllArgsConstructor
public class LinkMetadataAnnotations {

    // 启用友链健康监测
    private boolean enableFriendLinkHealthMonitor;

    // 友链页面地址
    private  String friendLinkUrl;

    /**
     * 使用Jackson将Map转换为对象
     */
    public static LinkMetadataAnnotations fromMap(Map<String, String> annotations) {
        // 处理null或空的Map
        if (annotations == null || annotations.isEmpty()) {
            return new LinkMetadataAnnotations(false, null);
        }

        // 仅处理目标字段，忽略其他所有字段
        boolean enable = false;
        String url = null;

        // 处理 enableFriendLinkHealthMonitor 字段
        if (annotations.containsKey(ANNOTATIONS_FIELD_ENABLE_MONITOR)) {
            String enableStr = annotations.get(ANNOTATIONS_FIELD_ENABLE_MONITOR);
            enable = "true".equalsIgnoreCase(enableStr); // 支持大小写
        }

        // 处理 friendLinkUrl 字段
        if (annotations.containsKey(ANNOTATIONS_FIELD_FRIEND_LINK_URL)) {
            url = annotations.get(ANNOTATIONS_FIELD_FRIEND_LINK_URL);
        }

        return new LinkMetadataAnnotations(enable, url);
    }
}
