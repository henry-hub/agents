package pub.ihub.agent.mcp.migrate;

import pub.ihub.integration.migrate.core.ProjectContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 从项目路径扫描构建上下文，供 {@link pub.ihub.integration.migrate.analyzer.ProjectAnalyzer} 使用。
 * <p>
 * ihub-migrate 的分析器不扫描文件系统，需要预填充 {@link ProjectContext}。
 * 本扫描器按优先级：
 * <ol>
 *   <li>读取 {@code build/ihub/project-meta.json}（iHubMeta 产物，结构化）</li>
 *   <li>回退解析 {@code gradle/libs.versions.toml} 的 [libraries] 提取 GAV</li>
 * </ol>
 * 仅提取 {@code group:artifact} 键（分析规则只匹配坐标，不校验版本）。
 */
public final class ProjectScanner {

    private static final Pattern TOML_MODULE = Pattern.compile(
        "module\\s*=\\s*['\"]([^'\"]+:[^'\"]+)['\"]");

    private ProjectScanner() {
    }

    /**
     * 扫描项目路径，构建 ProjectContext。
     *
     * @param projectPath 项目根路径
     * @return 填充好的 ProjectContext
     */
    public static ProjectContext scan(String projectPath) {
        Path root = Path.of(projectPath);
        String projectName = root.getFileName().toString();
        String buildTool = Files.exists(root.resolve("gradlew")) ? "gradle" : "maven";

        Map<String, String> dependencies = new LinkedHashMap<>();
        String javaVersion = "17";

        Path metaPath = root.resolve("build/ihub/project-meta.json");
        if (Files.exists(metaPath)) {
            String meta = readFile(metaPath);
            javaVersion = extractJsonString(meta, "javaVersion", "17");
            dependencies.putAll(extractGavsFromMeta(meta));
        }

        Path tomlPath = root.resolve("gradle/libs.versions.toml");
        if (dependencies.isEmpty() && Files.exists(tomlPath)) {
            dependencies.putAll(extractGavsFromToml(readFile(tomlPath)));
        }

        return new ProjectContext(
            projectName,
            root.toAbsolutePath().toString(),
            buildTool,
            javaVersion,
            dependencies,
            Map.of()
        );
    }

    /** 从 project-meta.json 的 dependencies.*.[].gav 提取 group:artifact */
    private static Map<String, String> extractGavsFromMeta(String meta) {
        Map<String, String> deps = new LinkedHashMap<>();
        Pattern gav = Pattern.compile("\"gav\"\\s*:\\s*\"([^\"]+)\"");
        Matcher m = gav.matcher(meta);
        while (m.find()) {
            String[] parts = m.group(1).split(":");
            if (parts.length >= 2) {
                deps.put(parts[0] + ":" + parts[1], parts.length >= 3 ? parts[2] : "");
            }
        }
        return deps;
    }

    /** 从 libs.versions.toml [libraries] 的 module = 'group:artifact' 提取坐标 */
    private static Map<String, String> extractGavsFromToml(String toml) {
        Map<String, String> deps = new LinkedHashMap<>();
        Matcher m = TOML_MODULE.matcher(toml);
        while (m.find()) {
            String ga = m.group(1);
            int colon = ga.indexOf(':');
            if (colon > 0 && colon < ga.length() - 1) {
                deps.put(ga, "");
            }
        }
        return deps;
    }

    private static String extractJsonString(String json, String key, String def) {
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
        Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : def;
    }

    private static String readFile(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            return "";
        }
    }
}
