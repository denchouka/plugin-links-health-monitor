package cool.tch.linkshealthmonitor.tasks;

import cool.tch.linkshealthmonitor.config.LinksHealthMonitorConfig;
import cool.tch.linkshealthmonitor.task.LinksHealthMonitorUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static cool.tch.linkshealthmonitor.constant.Constant.DEFAULT_FRIEND_LINK_ROUTES;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @Author Denchouka
 * @Date 2025/9/14 13:50
 * @Desc LinksHealthMonitorUtils测试
 */
public class LinksHealthMonitorUtilsTest {

    /**
     * 工具方法：拼接默认路由 + 自定义期望结果
     */
    private String[] concatDefaultAnd(String[] custom) {
        String[] result = new String[DEFAULT_FRIEND_LINK_ROUTES.length + custom.length];
        System.arraycopy(DEFAULT_FRIEND_LINK_ROUTES, 0, result, 0, DEFAULT_FRIEND_LINK_ROUTES.length);
        System.arraycopy(custom, 0, result, DEFAULT_FRIEND_LINK_ROUTES.length, custom.length);
        return result;
    }

    @Test
    void shouldReturnOnlyDefaultRoutesWhenRoutesIsNull() {
        LinksHealthMonitorConfig config = mock(LinksHealthMonitorConfig.class);
        when(config.getFriendLinkRoutes()).thenReturn(null);

        String[] result = LinksHealthMonitorUtils.getAllFriendLinkRoutes(config);
        assertArrayEquals(DEFAULT_FRIEND_LINK_ROUTES, result);
    }

    @Test
    void shouldReturnOnlyDefaultRoutesWhenRoutesIsEmpty() {
        LinksHealthMonitorConfig config = mock(LinksHealthMonitorConfig.class);
        when(config.getFriendLinkRoutes()).thenReturn(new String[0]);

        String[] result = LinksHealthMonitorUtils.getAllFriendLinkRoutes(config);
        assertArrayEquals(DEFAULT_FRIEND_LINK_ROUTES, result);
    }

    @Test
    void shouldNormalizeAndConcatValidRoutes() {
        LinksHealthMonitorConfig config = mock(LinksHealthMonitorConfig.class);
        when(config.getFriendLinkRoutes()).thenReturn(new String[]{
            "about",          // → /about
            "/blog/",         // → /blog
            "  project/test  ", // → /project/test
            "/a//b//c"        // → /a/b/c
        });

        String[] expected = concatDefaultAnd(new String[]{
            "/about", "/blog", "/project/test", "/a/b/c"
        });

        String[] result = LinksHealthMonitorUtils.getAllFriendLinkRoutes(config);
        assertArrayEquals(expected, result);
    }

    @Test
    void shouldConvertBackslashToSlash() {
        LinksHealthMonitorConfig config = mock(LinksHealthMonitorConfig.class);
        when(config.getFriendLinkRoutes()).thenReturn(new String[]{
            "\\links",        // → /links (但默认已有，去重)
            "about\\page",    // → /about/page
            "\\\\admin\\\\",  // → /admin
            "x\\y\\z\\"       // → /x/y/z
        });

        String[] expected = concatDefaultAnd(new String[]{
            "/about/page", "/admin", "/x/y/z"
        });

        String[] result = LinksHealthMonitorUtils.getAllFriendLinkRoutes(config);
        assertArrayEquals(expected, result);
    }

    @Test
    void shouldFilterAllFormsOfRootPath() {
        LinksHealthMonitorConfig config = mock(LinksHealthMonitorConfig.class);
        when(config.getFriendLinkRoutes()).thenReturn(new String[]{
            "/",              // 根
            "///",            // 多斜杠
            " / ",            // 带空格
            "\\",             // 单反斜杠
            "\\\\",           // 双反斜杠
            " \\\t\n ",       // 各种空白 + 反斜杠
            "",               // 空
            "   ",            // 空白
            null              // null
        });

        // 所有都被过滤，只返回默认
        String[] result = LinksHealthMonitorUtils.getAllFriendLinkRoutes(config);
        assertArrayEquals(DEFAULT_FRIEND_LINK_ROUTES, result);
    }

    @Test
    void shouldDeduplicateWithDefaultRoutes() {
        LinksHealthMonitorConfig config = mock(LinksHealthMonitorConfig.class);
        when(config.getFriendLinkRoutes()).thenReturn(new String[]{
            "/links",         // 重复
            "/links/",        // 重复（去尾）
            "\\link",         // 重复（转义后）
            "/extra"          // 新增
        });

        String[] expected = concatDefaultAnd(new String[]{
            "/extra"
        });

        String[] result = LinksHealthMonitorUtils.getAllFriendLinkRoutes(config);
        assertArrayEquals(expected, result);
    }

    @Test
    void shouldDeduplicateCustomInternal() {
        LinksHealthMonitorConfig config = mock(LinksHealthMonitorConfig.class);
        when(config.getFriendLinkRoutes()).thenReturn(new String[]{
            "/a", "/a/", "/a\\", "\\\\a", "/a"
        });

        String[] expected = concatDefaultAnd(new String[]{
            "/a"
        });

        String[] result = LinksHealthMonitorUtils.getAllFriendLinkRoutes(config);
        assertArrayEquals(expected, result);
    }

    @Test
    void shouldHandleMixedValidAndInvalid() {
        LinksHealthMonitorConfig config = mock(LinksHealthMonitorConfig.class);
        when(config.getFriendLinkRoutes()).thenReturn(new String[]{
            null,
            "",
            "   ",
            "/",
            "\\\\",
            "valid1",
            "\\valid2\\",
            "/dups/dups//",
            "valid1"  // 重复
        });

        String[] expected = concatDefaultAnd(new String[]{
            "/valid1", "/valid2", "/dups/dups"
        });

        String[] result = LinksHealthMonitorUtils.getAllFriendLinkRoutes(config);
        assertArrayEquals(expected, result);
    }

    @Test
    void shouldPreserveCaseAndSpecialChars() {
        LinksHealthMonitorConfig config = mock(LinksHealthMonitorConfig.class);
        when(config.getFriendLinkRoutes()).thenReturn(new String[]{
            "/API/V1",
            "/täg-test",
            "/path+with@special&chars"
        });

        String[] expected = concatDefaultAnd(new String[]{
            "/API/V1", "/täg-test", "/path+with@special&chars"
        });

        String[] result = LinksHealthMonitorUtils.getAllFriendLinkRoutes(config);
        assertArrayEquals(expected, result);
    }
}
