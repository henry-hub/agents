package pub.ihub.agent.mcp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pub.ihub.agent.mcp.catalog.CatalogService;
import pub.ihub.agent.mcp.module.ModuleService;
import pub.ihub.agent.mcp.tools.MigrateTools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * P2 端到端贯通验证：AI 通过 MCP 完成「查询组件 -> 分析项目 -> 模块编排」全链路。
 * 直接调用 @Tool 方法（Spring AI 注解的 Java 方法），验证三个工具协同工作。
 */
@SpringBootTest
class McpEndToEndIT {

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private MigrateTools migrateTools;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void catalogQueryReturnsRealComponents() {
        // 步骤 1：查询能力目录
        assertTrue(catalogService.count() > 0, "catalog 应含组件");
        assertTrue(!catalogService.search("ORM").isEmpty(), "应能搜到 ORM 组件");
        assertNotNull(catalogService.findById("infra-platform-spring-boot").orElse(null),
            "应能找到 Spring Boot 组件");
    }

    @Test
    void migrateAnalysisProducesRealReport() throws IOException {
        // 步骤 2：分析项目（fixture 含过时依赖）
        Path project = createFixtureProject();
        String result = migrateTools.analyzeProject(project.toString());

        JsonNode json = objectMapper.readTree(result);
        assertTrue(json.has("results"), "分析报告应含 results");
        assertTrue(json.get("totalIssues").asInt() > 0, "应检测到问题");
        assertTrue(json.has("summary"), "应含中文汇总");
    }

    @Test
    void moduleRegistryLoadsDescriptors() {
        // 步骤 3：模块编排（classpath 默认扫描，测试环境可能为 0，验证不报错）
        assertNotNull(moduleService.listAll(), "模块列表不应为 null");
    }

    private Path createFixtureProject() throws IOException {
        Path dir = Files.createTempDirectory("ihub-e2e-fixture");
        Path gradle = dir.resolve("gradle");
        Files.createDirectories(gradle);
        Files.writeString(gradle.resolve("libs.versions.toml"), """
            [libraries]
            fastjson1 = { module = 'com.alibaba:fastjson', version = '2.0.62' }
            junit = { module = 'junit:junit', version = '4.13.2' }
            """);
        Files.createFile(dir.resolve("gradlew"));
        return dir;
    }
}
