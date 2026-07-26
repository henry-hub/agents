package pub.ihub.agent.mcp.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import pub.ihub.agent.mcp.migrate.ProjectScanner;
import pub.ihub.integration.migrate.analyzer.ProjectAnalyzer;
import pub.ihub.integration.migrate.core.AnalysisReport;
import pub.ihub.integration.migrate.core.ProjectContext;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * IHub 迁移分析 MCP 工具，为 AI 提供遗留系统迁移的结构化指引与真实分析能力。
 * <p>
 * 静态指南工具（listMigrationCategories / getMigrationGuide / getMigrationRoadmap）
 * 提供迁移规则目录和步骤描述；analyzeProject 调用 ihub-migrate-analyzer 真实分析项目，
 * 返回结构化报告（问题列表、严重度、修复建议）。
 */
@Component
public class MigrateTools {

    private final ObjectMapper objectMapper;

    public MigrateTools(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Tool(description = "分析指定路径的 Java/Gradle 项目，运行 IHub 迁移规则，返回结构化分析报告。"
            + "扫描项目的依赖和 Java 版本，检测过时依赖（如 fastjson/log4j/junit）和 Java 版本是否满足 17+ LTS。"
            + "报告含问题列表（severity/description/fix）、汇总和阻断项标记。"
            + "可结合 getMigrationGuide 制定迁移策略。")
    public String analyzeProject(@ToolParam(description = "项目根路径（绝对路径）") String projectPath) {
        ProjectContext ctx;
        try {
            ctx = ProjectScanner.scan(projectPath);
        } catch (Exception e) {
            return toJson(Map.of(
                "error", "Failed to scan project: " + e.getMessage(),
                "hint", "Ensure the path is a valid project root with gradle/libs.versions.toml or build/ihub/project-meta.json"
            ));
        }
        ProjectAnalyzer analyzer = new ProjectAnalyzer();
        AnalysisReport report = analyzer.analyze(ctx);
        return toJson(toReportMap(report, ctx));
    }

    @Tool(description = "列出 IHub 支持的迁移分析规则分类（category），"
            + "包括依赖分析、代码模式检测、配置迁移、构建工具迁移等。"
            + "可进一步用 getMigrationGuide 获取某个分类的详细迁移指南。")
    public String listMigrationCategories() {
        List<Map<String, Object>> categories = List.of(
            category("DEPENDENCY", "依赖迁移",
                "检测过时或不兼容的依赖，推荐 IHub catalog 中的替代方案",
                List.of("检测 Druid 版本兼容性", "Spring Boot 2→4 starter 重命名", "Jakarta EE namespace 变更")),
            category("CODE_PATTERN", "代码模式迁移",
                "识别需要重构的代码模式，如废弃 API、不推荐用法",
                List.of("Javax→Jakarta 包名替换", "Spring Security 5→6 配置 DSL 变更", "MyBatis Plus 3.x API 调整")),
            category("CONFIG", "配置迁移",
                "检测需要更新的 application.yml/properties 配置项",
                List.of("Spring Boot 属性重命名", "数据库连接池配置格式变更", "日志框架配置迁移")),
            category("BUILD", "构建工具迁移",
                "Maven 迁移到 Gradle、Groovy DSL 迁移到 Kotlin DSL 等构建脚本现代化",
                List.of("Maven POM → Gradle KTS 转换", "依赖管理从 BOM 迁移", "插件版本统一到 IHub 插件体系"))
        );
        return toJson(Map.of(
            "categories", categories,
            "analysis_tool", "ihub-migrate-core (pub.ihub.integration:ihub-migrate-core)",
            "hint", "Use getMigrationGuide(category) for detailed migration instructions"
        ));
    }

    @Tool(description = "获取指定迁移分类的详细指南，包含迁移步骤、常见问题、IHub 推荐方案和参考资源。"
            + "分类值：DEPENDENCY、CODE_PATTERN、CONFIG、BUILD。")
    public String getMigrationGuide(
            @ToolParam(description = "迁移分类，如 DEPENDENCY、CODE_PATTERN、CONFIG、BUILD") String category) {
        return switch (category.toUpperCase()) {
            case "DEPENDENCY" -> toJson(dependencyGuide());
            case "CODE_PATTERN" -> toJson(codePatternGuide());
            case "CONFIG" -> toJson(configGuide());
            case "BUILD" -> toJson(buildGuide());
            default -> toJson(Map.of("error", "Unknown category: " + category,
                "hint", "Use listMigrationCategories to see available categories"));
        };
    }

    @Tool(description = "获取将遗留项目迁移到 IHub 技术栈的完整路线图，"
            + "包括评估阶段、迁移优先级和各阶段里程碑。")
    public String getMigrationRoadmap() {
        List<Map<String, Object>> phases = List.of(
            phase(1, "评估", "现状扫描与风险识别",
                List.of(
                    "运行 ihub-migrate-core 分析器扫描项目",
                    "统计 BLOCKER/CRITICAL/WARNING 问题数量",
                    "识别核心依赖与 IHub catalog 的映射关系"
                )),
            phase(2, "构建现代化", "Gradle KTS + IHub 插件体系",
                List.of(
                    "迁移到 Gradle Kotlin DSL",
                    "引入 pub.ihub.plugin.ihub-settings",
                    "统一 libs.versions.toml 版本管理"
                )),
            phase(3, "依赖替换", "替换过时依赖为 IHub catalog 推荐方案",
                List.of(
                    "按 catalog domain 逐步替换：data → security → distributed",
                    "运行 OpenRewrite 配方自动化重写",
                    "通过 CI 验证每批次依赖替换"
                )),
            phase(4, "业务模块化", "引入 ihub-module-* 业务模块",
                List.of(
                    "按 modules 仓库描述符集成 IAM/Org 等通用模块",
                    "配置 module-descriptor.json 使 AI 可编排",
                    "接入 MCP Server 实现 AI 驱动的业务操作"
                ))
        );
        return toJson(Map.of(
            "title", "IHub 迁移路线图",
            "total_phases", phases.size(),
            "phases", phases,
            "tools", Map.of(
                "analysis", "ihub-migrate-core",
                "rewrite", "ihub-migrate-rewrite (OpenRewrite)",
                "runtime", "agents/java-agent",
                "ai_orchestration", "agents/mcp-server"
            )
        ));
    }

    // ---- guide builders ----

    private Map<String, Object> dependencyGuide() {
        Map<String, Object> guide = new LinkedHashMap<>();
        guide.put("category", "DEPENDENCY");
        guide.put("title", "依赖迁移指南");
        guide.put("steps", List.of(
            "1. 运行 MigrationAnalyzer 扫描 pom.xml / build.gradle 中的依赖",
            "2. 对照 IHub catalog (searchCatalog) 找到推荐替代方案",
            "3. 使用 gradle_ref 替换原始 GAV 坐标",
            "4. 引入 libs.versions.toml 统一版本管理"
        ));
        guide.put("common_replacements", List.of(
            Map.of("from", "com.baomidou:mybatis-plus-boot-starter", "to", "mybatis-plus (catalog gradle_ref)", "domain", "data"),
            Map.of("from", "org.springframework.security:spring-security-*", "to", "spring-security (catalog)", "domain", "security"),
            Map.of("from", "com.alibaba:druid-spring-boot-starter", "to", "druid (catalog)", "domain", "data")
        ));
        guide.put("ihub_tool", "searchCatalog in CatalogTools");
        return guide;
    }

    private Map<String, Object> codePatternGuide() {
        Map<String, Object> guide = new LinkedHashMap<>();
        guide.put("category", "CODE_PATTERN");
        guide.put("title", "代码模式迁移指南");
        guide.put("steps", List.of(
            "1. 识别 javax.* 引用，批量替换为 jakarta.*",
            "2. 更新 Spring Security 配置：WebSecurityConfigurerAdapter → SecurityFilterChain",
            "3. 检查并更新 MyBatis Plus 3.x 废弃 API",
            "4. 使用 OpenRewrite (ihub-migrate-rewrite) 自动化重写"
        ));
        guide.put("openrewrite_recipes", List.of(
            "org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta",
            "org.openrewrite.java.spring.security6.UpgradeSpringSecurity_6_0",
            "org.openrewrite.gradle.MigrateToGradleLocalJavaToolchains"
        ));
        return guide;
    }

    private Map<String, Object> configGuide() {
        Map<String, Object> guide = new LinkedHashMap<>();
        guide.put("category", "CONFIG");
        guide.put("title", "配置迁移指南");
        guide.put("steps", List.of(
            "1. 运行 Spring Boot ConfigurationMetadataAnnotationProcessor 检测废弃属性",
            "2. 参考各模块 config_prefix (getModule) 了解 IHub 配置规范",
            "3. 迁移数据源配置到 HikariCP 标准格式",
            "4. 统一日志配置到 logback-spring.xml 格式"
        ));
        guide.put("ihub_config_prefixes", Map.of(
            "iam-user", "ihub.module.user",
            "data-orm", "ihub.data",
            "security-authn", "ihub.security"
        ));
        return guide;
    }

    private Map<String, Object> buildGuide() {
        Map<String, Object> guide = new LinkedHashMap<>();
        guide.put("category", "BUILD");
        guide.put("title", "构建工具迁移指南");
        guide.put("steps", List.of(
            "1. 将 pom.xml 转为 build.gradle.kts（保留 Maven 发布配置）",
            "2. 在 settings.gradle.kts 引入 pub.ihub.plugin.ihub-settings",
            "3. 在 gradle/libs.versions.toml 统一声明版本（遵循 IHub 规范）",
            "4. 按模块职责引入对应 IHub 插件（ihub-java, ihub-publish 等）"
        ));
        guide.put("ihub_plugins", Map.of(
            "ihub-settings", "多模块项目基础设置，自动发现 ihub-* 子模块",
            "ihub-java", "Java 编译、代码质量、Jacoco",
            "ihub-publish", "Maven Central 发布",
            "ihub-boot", "Spring Boot 应用打包"
        ));
        return guide;
    }

    // ---- helpers ----

    /** 将 AnalysisReport 转为可序列化的 Map（含上下文与统计） */
    private Map<String, Object> toReportMap(AnalysisReport report, ProjectContext ctx) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("projectName", report.projectName());
        m.put("projectPath", report.projectPath());
        m.put("analyzedAt", report.analyzedAt() != null ? report.analyzedAt().toString() : null);
        m.put("summary", report.summary());
        m.put("totalIssues", report.totalIssues());
        m.put("hasBlockers", report.hasBlockers());
        m.put("scannedDependencies", ctx.dependencies() != null ? ctx.dependencies().size() : 0);
        m.put("javaVersion", ctx.javaVersion());
        m.put("buildTool", ctx.buildTool());
        List<Map<String, Object>> results = report.results().stream()
            .map(r -> {
                Map<String, Object> rm = new LinkedHashMap<>();
                rm.put("ruleId", r.ruleId());
                rm.put("issueCount", r.issues() != null ? r.issues().size() : 0);
                rm.put("issues", r.issues() != null ? r.issues() : List.of());
                rm.put("suggestions", r.suggestions() != null ? r.suggestions() : List.of());
                return rm;
            })
            .toList();
        m.put("results", results);
        return m;
    }

    private Map<String, Object> category(String id, String name, String description, List<String> examples) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("name", name);
        m.put("description", description);
        m.put("examples", examples);
        return m;
    }

    private Map<String, Object> phase(int num, String name, String goal, List<String> actions) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("phase", num);
        m.put("name", name);
        m.put("goal", goal);
        m.put("actions", actions);
        return m;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException ex) {
            return "{\"error\": \"serialization failed\"}";
        }
    }
}
