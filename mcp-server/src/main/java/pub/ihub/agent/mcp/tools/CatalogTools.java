package pub.ihub.agent.mcp.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import pub.ihub.agent.mcp.catalog.CatalogEntry;
import pub.ihub.agent.mcp.catalog.CatalogService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * IHub 能力目录 MCP 工具，提供领域浏览、组件搜索和详情查询能力。
 * <p>
 * 数据来源：{@link CatalogService} 加载的 catalog.json，
 * 该文件由 libs 仓库的 merge-catalog.py 从各 domain JSON 合并生成。
 */
@Component
public class CatalogTools {

    private final CatalogService catalogService;
    private final ObjectMapper objectMapper;

    public CatalogTools(CatalogService catalogService, ObjectMapper objectMapper) {
        this.catalogService = catalogService;
        this.objectMapper = objectMapper;
    }

    @Tool(description = "列出 IHub 能力目录的所有领域（domain）及每个领域的组件数量。"
            + "可进一步用 listDomainComponents 查看某领域的具体组件。")
    public String listDomains() {
        List<String> domains = catalogService.listDomains();
        List<Map<String, Object>> result = domains.stream().map(d -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("domain", d);
            m.put("count", catalogService.listByDomain(d).size());
            return m;
        }).toList();
        return toJson(Map.of("domains", result, "total_components", catalogService.count()));
    }

    @Tool(description = "列出指定领域（domain）中的所有组件，返回 id、name、description、status、stage。"
            + "可用领域：infrastructure, data, ddd, mapping, security, distributed, "
            + "workflow, observability, documentation, testing, messaging, utilities, meta。")
    public String listDomainComponents(@ToolParam(description = "领域名称，如 data、security") String domain) {
        List<CatalogEntry> entries = catalogService.listByDomain(domain);
        if (entries.isEmpty()) {
            return toJson(Map.of("domain", domain, "components", List.of(),
                    "hint", "Domain not found. Use listDomains to see available domains."));
        }
        List<Map<String, Object>> summaries = entries.stream().map(this::toSummary).toList();
        return toJson(Map.of("domain", domain, "components", summaries));
    }

    @Tool(description = "在 IHub 能力目录中搜索组件。支持多关键词（空格分隔，AND 语义），"
            + "在组件 id、名称、描述、用途、标签中匹配。"
            + "例如：'ORM 分页'、'JWT 认证'、'分布式锁 Redis'。")
    public String searchCatalog(@ToolParam(description = "搜索关键词，支持中英文，多词用空格分隔") String query) {
        List<CatalogEntry> results = catalogService.search(query);
        List<Map<String, Object>> summaries = results.stream().map(this::toSummary).toList();
        return toJson(Map.of("query", query, "count", results.size(), "results", summaries));
    }

    @Tool(description = "获取 IHub 能力目录中某个组件的完整详情，"
            + "包含 ai_context（集成代码示例）、gradle_ref（Gradle 别名）、"
            + "alternatives（替代方案）、related_plugins（关联插件）等。"
            + "先用 searchCatalog 找到 id，再用此工具获取详情。")
    public String getComponent(@ToolParam(description = "组件 id，如 mybatis-plus、spring-security、hutool") String id) {
        Optional<CatalogEntry> entry = catalogService.findById(id);
        if (entry.isEmpty()) {
            return toJson(Map.of("error", "Component not found: " + id,
                    "hint", "Use searchCatalog to find the correct id."));
        }
        CatalogEntry e = entry.get();
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("id", e.id());
        detail.put("name", e.name());
        detail.put("name_en", e.nameEn());
        detail.put("domain", e.domain());
        detail.put("type", e.type());
        detail.put("status", e.status());
        detail.put("stage", e.stage());
        detail.put("description", e.description());
        detail.put("use_case", e.useCase());
        detail.put("ai_context", e.aiContext());
        detail.put("gradle_ref", e.gradleRefs());
        detail.put("version_ref", e.versionRef());
        detail.put("alternatives", e.alternatives());
        detail.put("dependencies", e.dependencies());
        detail.put("related_plugins", e.relatedPlugins());
        detail.put("tags", e.tags());
        detail.put("doc_url", e.docUrl());
        detail.put("ihub_layer", e.ihubLayer());
        return toJson(detail);
    }

    // ---- helpers ----

    private Map<String, Object> toSummary(CatalogEntry e) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", e.id());
        m.put("name", e.name());
        m.put("description", e.description());
        m.put("status", e.status());
        m.put("stage", e.stage());
        m.put("gradle_ref", e.gradleRefs());
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
