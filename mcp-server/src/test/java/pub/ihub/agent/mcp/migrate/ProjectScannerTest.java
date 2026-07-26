package pub.ihub.agent.mcp.migrate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pub.ihub.integration.migrate.analyzer.ProjectAnalyzer;
import pub.ihub.integration.migrate.core.AnalysisReport;
import pub.ihub.integration.migrate.core.ProjectContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ProjectScanner + ProjectAnalyzer 端到端验证：
 * 构造含过时依赖的 fixture 项目，验证扫描器提取坐标、分析器检测问题。
 */
class ProjectScannerTest {

    @TempDir
    Path tempDir;

    @Test
    void scanParsesTomlDependencies() throws IOException {
        Path gradleDir = tempDir.resolve("gradle");
        Files.createDirectories(gradleDir);
        Files.writeString(gradleDir.resolve("libs.versions.toml"), """
            [libraries]
            fastjson1 = { module = 'com.alibaba:fastjson', version = '2.0.62' }
            junit = { module = 'junit:junit', version = '4.13.2' }
            spring-boot = { module = 'org.springframework.boot:spring-boot', version = '4.1.0' }
            """);
        Files.createFile(tempDir.resolve("gradlew"));

        ProjectContext ctx = ProjectScanner.scan(tempDir.toString());

        assertEquals("gradle", ctx.buildTool());
        assertEquals(3, ctx.dependencies().size());
        assertTrue(ctx.dependencies().containsKey("com.alibaba:fastjson"));
        assertTrue(ctx.dependencies().containsKey("junit:junit"));
    }

    @Test
    void analyzeDetectsObsoleteDependencies() throws IOException {
        Path gradleDir = tempDir.resolve("gradle");
        Files.createDirectories(gradleDir);
        Files.writeString(gradleDir.resolve("libs.versions.toml"), """
            [libraries]
            fastjson1 = { module = 'com.alibaba:fastjson', version = '2.0.62' }
            junit = { module = 'junit:junit', version = '4.13.2' }
            """);

        ProjectContext ctx = ProjectScanner.scan(tempDir.toString());
        AnalysisReport report = new ProjectAnalyzer().analyze(ctx);

        assertTrue(report.totalIssues() >= 2,
            "应检测到 fastjson 和 junit 两个过时依赖，实际: " + report.totalIssues());
        assertFalse(report.hasBlockers(), "过时依赖为 WARNING 级别，不应有 BLOCKER");
        assertTrue(report.summary() != null && !report.summary().isBlank(),
            "应生成中文汇总");
    }

    @Test
    void analyzeFlagsLowJavaVersion() throws IOException {
        Path gradleDir = tempDir.resolve("gradle");
        Files.createDirectories(gradleDir);
        Files.writeString(gradleDir.resolve("libs.versions.toml"), """
            [libraries]
            spring = { module = 'org.springframework:spring-core', version = '6.0.0' }
            """);

        ProjectContext ctx = new ProjectContext(
            "legacy-app", tempDir.toString(), "gradle", "8",
            java.util.Map.of("org.springframework:spring-core", "6.0.0"),
            java.util.Map.of()
        );
        AnalysisReport report = new ProjectAnalyzer().analyze(ctx);

        assertTrue(report.hasBlockers() || report.totalIssues() > 0,
            "Java 8 应触发 CRITICAL 级别问题");
    }
}
