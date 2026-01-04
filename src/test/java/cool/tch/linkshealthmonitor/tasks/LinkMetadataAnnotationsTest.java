package cool.tch.linkshealthmonitor.tasks;

import cool.tch.linkshealthmonitor.extension.LinkMetadataAnnotations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * @Author Denchouka
 * @Date 2026/1/4 22:04
 * @Desc LinkMetadataAnnotations有关测试
 */
public class LinkMetadataAnnotationsTest {

    private LinkMetadataAnnotations linkMetadataAnnotations;
    private Map<String, String> testAnnotations;

    @BeforeEach
    void setUp() {
        testAnnotations = new HashMap<>();
    }

    @Test
    @DisplayName("测试fromMap方法 - 正常情况：Map包含所有字段")
    void testFromMap_withAllFields() {
        // 准备测试数据
        testAnnotations.put("enableFriendLinkHealthMonitor", "true");
        testAnnotations.put("friendLinkUrl", "https://example.com/friends");

        // 执行方法
        LinkMetadataAnnotations result = LinkMetadataAnnotations.fromMap(testAnnotations);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.isEnableFriendLinkHealthMonitor());
        assertEquals("https://example.com/friends", result.getFriendLinkUrl());
    }

    @Test
    @DisplayName("测试fromMap方法 - boolean字段值为false")
    void testFromMap_withBooleanFalse() {
        // 准备测试数据
        testAnnotations.put("enableFriendLinkHealthMonitor", "false");
        testAnnotations.put("friendLinkUrl", "https://example.com/links");

        // 执行方法
        LinkMetadataAnnotations result = LinkMetadataAnnotations.fromMap(testAnnotations);

        // 验证结果
        assertNotNull(result);
        assertFalse(result.isEnableFriendLinkHealthMonitor());
        assertEquals("https://example.com/links", result.getFriendLinkUrl());
    }

    @Test
    @DisplayName("测试fromMap方法 - boolean字段值为大小写混合")
    void testFromMap_withBooleanCaseInsensitive() {
        // 准备测试数据 - 测试大小写不敏感
        testAnnotations.put("enableFriendLinkHealthMonitor", "TRUE");
        testAnnotations.put("friendLinkUrl", "https://example.com");

        // 执行方法
        LinkMetadataAnnotations result = LinkMetadataAnnotations.fromMap(testAnnotations);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.isEnableFriendLinkHealthMonitor());
    }

    @Test
    @DisplayName("测试fromMap方法 - boolean字段值为数字1")
    void testFromMap_withBooleanAsNumberOne() {
        // 准备测试数据 - Jackson会将"1"解析为false
        testAnnotations.put("enableFriendLinkHealthMonitor", "1");

        // 执行方法
        LinkMetadataAnnotations result = LinkMetadataAnnotations.fromMap(testAnnotations);

        // 验证结果
        assertNotNull(result);
        assertFalse(result.isEnableFriendLinkHealthMonitor());
    }

    @Test
    @DisplayName("测试fromMap方法 - boolean字段值为数字0")
    void testFromMap_withBooleanAsNumberZero() {
        // 准备测试数据 - Jackson会将"0"解析为false
        testAnnotations.put("enableFriendLinkHealthMonitor", "0");

        // 执行方法
        LinkMetadataAnnotations result = LinkMetadataAnnotations.fromMap(testAnnotations);

        // 验证结果 - 注意：Jackson会将"0"解析为false
        assertNotNull(result);
        assertFalse(result.isEnableFriendLinkHealthMonitor());
    }

    @Test
    @DisplayName("测试fromMap方法 - 只有boolean字段，没有URL字段")
    void testFromMap_withOnlyBooleanField() {
        // 准备测试数据
        testAnnotations.put("enableFriendLinkHealthMonitor", "true");

        // 执行方法
        LinkMetadataAnnotations result = LinkMetadataAnnotations.fromMap(testAnnotations);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.isEnableFriendLinkHealthMonitor());
        assertNull(result.getFriendLinkUrl());
    }

    @Test
    @DisplayName("测试fromMap方法 - 只有URL字段，没有boolean字段")
    void testFromMap_withOnlyUrlField() {
        // 准备测试数据
        testAnnotations.put("friendLinkUrl", "https://example.com");

        // 执行方法
        LinkMetadataAnnotations result = LinkMetadataAnnotations.fromMap(testAnnotations);

        // 验证结果 - boolean字段应该为默认值false
        assertNotNull(result);
        assertFalse(result.isEnableFriendLinkHealthMonitor());
        assertEquals("https://example.com", result.getFriendLinkUrl());
    }

    @Test
    @DisplayName("测试fromMap方法 - 空Map")
    void testFromMap_withEmptyMap() {
        // 执行方法
        LinkMetadataAnnotations result = LinkMetadataAnnotations.fromMap(Collections.emptyMap());

        // 验证结果 - 所有字段都应该是默认值
        assertNotNull(result);
        assertFalse(result.isEnableFriendLinkHealthMonitor());
        assertNull(result.getFriendLinkUrl());
    }

    @Test
    @DisplayName("测试fromMap方法 - null Map")
    void testFromMap_withNullMap() {
        // 执行方法
        LinkMetadataAnnotations result = LinkMetadataAnnotations.fromMap(null);

        // 验证结果 - 应该返回默认对象
        assertNotNull(result);
        assertFalse(result.isEnableFriendLinkHealthMonitor());
        assertNull(result.getFriendLinkUrl());
    }

    @Test
    @DisplayName("测试fromMap方法 - 字段值为null")
    void testFromMap_withNullValues() {
        // 准备测试数据 - 注意：Jackson处理原始类型boolean的null值会抛出异常
        testAnnotations.put("enableFriendLinkHealthMonitor", null);
        testAnnotations.put("friendLinkUrl", null);

        // 执行方法 - 由于原始类型boolean不能为null，这里会抛出异常并被捕获
        LinkMetadataAnnotations result = LinkMetadataAnnotations.fromMap(testAnnotations);

        // 验证结果 - 由于异常被捕获，应该返回默认对象
        assertNotNull(result);
        assertFalse(result.isEnableFriendLinkHealthMonitor());
        assertNull(result.getFriendLinkUrl());
    }

    @Test
    @DisplayName("测试fromMap方法 - boolean字段值为空字符串")
    void testFromMap_withEmptyBooleanString() {
        // 准备测试数据
        testAnnotations.put("enableFriendLinkHealthMonitor", "");
        testAnnotations.put("friendLinkUrl", "https://example.com");

        // 执行方法
        LinkMetadataAnnotations result = LinkMetadataAnnotations.fromMap(testAnnotations);

        // 验证结果 - 空字符串会被Jackson解析为false
        assertNotNull(result);
        assertFalse(result.isEnableFriendLinkHealthMonitor());
        assertEquals("https://example.com", result.getFriendLinkUrl());
    }

    @Test
    @DisplayName("测试fromMap方法 - URL字段值为空字符串")
    void testFromMap_withEmptyUrlString() {
        // 准备测试数据
        testAnnotations.put("enableFriendLinkHealthMonitor", "true");
        testAnnotations.put("friendLinkUrl", "");

        // 执行方法
        LinkMetadataAnnotations result = LinkMetadataAnnotations.fromMap(testAnnotations);

        // 验证结果 - 空字符串会被解析为空字符串，而不是null
        assertNotNull(result);
        assertTrue(result.isEnableFriendLinkHealthMonitor());
        assertEquals("", result.getFriendLinkUrl());
    }

    @Test
    @DisplayName("测试fromMap方法 - 包含额外字段")
    void testFromMap_withExtraFields() {
        // 准备测试数据 - 包含注解中没有定义的额外字段
        testAnnotations.put("enableFriendLinkHealthMonitor", "true");
        testAnnotations.put("friendLinkUrl", "https://example.com");
        testAnnotations.put("extraField", "extraValue");
        testAnnotations.put("anotherField", "anotherValue");

        // 执行方法 - Jackson默认会忽略未知字段，所以不应该抛出异常
        LinkMetadataAnnotations result = LinkMetadataAnnotations.fromMap(testAnnotations);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.isEnableFriendLinkHealthMonitor());
        assertEquals("https://example.com", result.getFriendLinkUrl());
    }

    @Test
    @DisplayName("测试fromMap方法 - 字段名大小写敏感测试")
    void testFromMap_withCaseSensitiveFieldNames() {
        // 准备测试数据 - 使用不同大小写的字段名
        testAnnotations.put("EnableFriendLinkHealthMonitor", "true"); // 首字母大写
        testAnnotations.put("FRIENDLINKURL", "https://example.com"); // 全大写

        // 执行方法 - Jackson默认是大小写敏感的，所以这些字段不会被识别
        LinkMetadataAnnotations result = LinkMetadataAnnotations.fromMap(testAnnotations);

        // 验证结果 - 字段不会被识别，所以使用默认值
        assertNotNull(result);
        assertFalse(result.isEnableFriendLinkHealthMonitor()); // 默认值false
        assertNull(result.getFriendLinkUrl()); // 默认值null
    }

    @Test
    @DisplayName("测试fromMap方法 - 使用无效的boolean值")
    void testFromMap_withInvalidBooleanValue() {
        // 准备测试数据 - 使用无效的boolean字符串
        testAnnotations.put("enableFriendLinkHealthMonitor", "invalid");
        testAnnotations.put("friendLinkUrl", "https://example.com");

        // 执行方法 - Jackson会将无效的boolean字符串解析为false
        LinkMetadataAnnotations result = LinkMetadataAnnotations.fromMap(testAnnotations);

        // 验证结果
        assertNotNull(result);
        assertFalse(result.isEnableFriendLinkHealthMonitor()); // "invalid" 被解析为 false
        assertEquals("https://example.com", result.getFriendLinkUrl());
    }

    @ParameterizedTest
    @MethodSource("booleanTestCases")
    @DisplayName("参数化测试 - boolean字段各种值")
    void testFromMap_parameterizedBooleanValues(String input, boolean expected) {
        // 准备测试数据
        testAnnotations.put("enableFriendLinkHealthMonitor", input);

        // 执行方法
        LinkMetadataAnnotations result = LinkMetadataAnnotations.fromMap(testAnnotations);

        // 验证结果
        assertNotNull(result);
        assertEquals(expected, result.isEnableFriendLinkHealthMonitor());
    }

    private static Stream<Arguments> booleanTestCases() {
        return Stream.of(
            arguments("true", true),
            arguments("false", false),
            arguments("TRUE", true),
            arguments("FALSE", false),
            arguments("True", true),
            arguments("False", false),
            arguments("1", false),     // Jackson将"1"解析为true
            arguments("0", false),    // Jackson将"0"解析为false
            arguments("yes", false),  // "yes"不是有效的boolean字符串，Jackson解析为false
            arguments("no", false),   // "no"不是有效的boolean字符串，Jackson解析为false
            arguments("", false),     // 空字符串解析为false
            arguments(" ", false)     // 空格字符串解析为false
        );
    }

    @Test
    @DisplayName("测试构造函数和getter/setter")
    void testConstructorAndGettersSetters() {
        // 测试全参构造函数
        LinkMetadataAnnotations annotations = new LinkMetadataAnnotations(true, "https://test.com");

        assertEquals(true, annotations.isEnableFriendLinkHealthMonitor());
        assertEquals("https://test.com", annotations.getFriendLinkUrl());

        // 测试@Data注解生成的equals和hashCode方法
        LinkMetadataAnnotations sameAnnotations = new LinkMetadataAnnotations(true, "https://test.com");
        assertEquals(annotations, sameAnnotations);
        assertEquals(annotations.hashCode(), sameAnnotations.hashCode());

        // 测试不同对象
        LinkMetadataAnnotations differentAnnotations = new LinkMetadataAnnotations(false, "https://different.com");
        assertNotEquals(annotations, differentAnnotations);
    }

    @Test
    @DisplayName("测试toString方法")
    void testToString() {
        LinkMetadataAnnotations annotations = new LinkMetadataAnnotations(true, "https://example.com");
        String toStringResult = annotations.toString();

        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("enableFriendLinkHealthMonitor=true"));
        assertTrue(toStringResult.contains("friendLinkUrl=https://example.com"));
    }

    @Test
    @DisplayName("测试并发访问 - fromMap方法线程安全")
    void testConcurrentAccess() throws InterruptedException {
        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];
        LinkMetadataAnnotations[] results = new LinkMetadataAnnotations[threadCount];

        // 创建多个线程同时调用fromMap
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            Map<String, String> threadAnnotations = new HashMap<>();
            threadAnnotations.put("enableFriendLinkHealthMonitor", index % 2 == 0 ? "true" : "false");
            threadAnnotations.put("friendLinkUrl", "https://thread-" + index + ".com");

            threads[i] = new Thread(() -> {
                results[index] = LinkMetadataAnnotations.fromMap(threadAnnotations);
            });
        }

        // 启动所有线程
        for (Thread thread : threads) {
            thread.start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }

        // 验证所有线程都得到了正确的结果
        for (int i = 0; i < threadCount; i++) {
            assertNotNull(results[i]);
            assertEquals(i % 2 == 0, results[i].isEnableFriendLinkHealthMonitor());
            assertEquals("https://thread-" + i + ".com", results[i].getFriendLinkUrl());
        }
    }

}
