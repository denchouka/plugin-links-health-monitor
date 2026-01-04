package cool.tch.linkshealthmonitor.tasks;

import org.junit.jupiter.api.Test;
import java.net.HttpURLConnection;
import java.net.URI;

import static cool.tch.linkshealthmonitor.constant.Constant.HTTP_REQUEST_METHOD_GET;
import static cool.tch.linkshealthmonitor.constant.Constant.HTTP_TIMEOUT_MS;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @Author Denchouka
 * @Date 2025/9/14 13:50
 * @Desc LinksHealthMonitorUtils测试
 */
public class LinksHealthMonitorUtilsTest {

    @Test
    public void isUrlAccessibleTest() {
        // assertTrue(isUrlAccessible("https://tch.cool") == 200);
    }

    public static int isUrlAccessible(String url) {

        try{
            HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
            // 基础设置
            connection.setRequestMethod(HTTP_REQUEST_METHOD_GET);
            connection.setConnectTimeout(HTTP_TIMEOUT_MS);
            connection.setReadTimeout(HTTP_TIMEOUT_MS);
            // 自动跟随重定向
            connection.setInstanceFollowRedirects(true);
            // 完整的浏览器请求头（模拟Chrome最新版）
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36");
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
            connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br, zstd");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
            connection.setRequestProperty("Sec-Fetch-Dest", "document");
            connection.setRequestProperty("Sec-Fetch-Mode", "navigate");
            connection.setRequestProperty("Sec-Fetch-Site", "same-origin");
            connection.setRequestProperty("Sec-Fetch-User", "?1");
            connection.setRequestProperty("referer", "https://tch.cool");
            return connection.getResponseCode();
        } catch (Exception e) {
            return 500;
        }
    }
}
