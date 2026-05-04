package pub.ihub.agent.mcp.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import pub.ihub.agent.mcp.module.ModuleDescriptor;
import pub.ihub.agent.mcp.module.ModuleService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * IHub 业务模块 MCP 工具，提供模块发现、描述符查询和 MCP 工具列表能力。
 * <p>
 * 数据来源：{@link ModuleService} 扫描 classpath 中所有
 * {@code META-INF/ihub/module-descriptor.json} 得到的注册表。
 */
@Component
public class ModuleTools {

    private final ModuleService moduleService;
    private final ObjectMapper objectMapper;

    public ModuleTools(ModuleService moduleService, ObjectMapper objectMapper) {
        this.moduleService = moduleService;
        this.objectMapper = objectMapper;
    }

    @Tool(description = "列出所有可用的 IHub 业务模块，返回模块 id、名称、领域、状态和简要描述。"
            + "可进一步用 getModule 获取完整描述符，或用 getModuleTools 获取 MCP 工具列表。")
    public String listModules() {
        List<ModuleDescriptor> all = moduleService.listAll();
        List<Map<String, Object>> summaries = all.stream().map(this::toSummary).toList();
        return toJson(Map.of("total", all.size(), "modules", summaries));
    }

    @Tool(description = "获取指定业务模块的完整描述符，包含 ai_context（集成指南）、"
            + "capabilities（API/事件清单）、dependencies（模块依赖）、"
            + "maven 坐标、配置前缀等。先用 listModules 找到模块 id。")
    public String getModule(
            @ToolParam(description = "模块 id，如 iam-user、iam-role、org-tenant") String moduleId) {
        Optional<ModuleDescriptor> found = moduleService.findById(moduleId);
        if (found.isEmpty()) {
            return toJson(Map.of("error", "Module not found: " + moduleId,
                    "hint", "Use listModules to see all available module ids."));
        }
        ModuleDescriptor m = found.get();
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("id", m.id());
        detail.put("name", m.name());
        detail.put("name_en", m.nameEn());
        detail.put("version", m.version());
        detail.put("domain", m.domain());
        detail.put("status", m.status());
        detail.put("description", m.description());
        detail.put("ai_context", m.aiContext());
        detail.put("capabilities", m.capabilities());
        detail.put("dependencies", m.dependencies());
        detail.put("maven", m.maven());
        detail.put("gradle_ref", m.gradleRef());
        detail.put("config_prefix", m.configPrefix());
        detail.put("tags", m.tags());
        detail.put("mcp_tools_count", m.mcpTools() != null ? m.mcpTools().size() : 0);
        return toJson(detail);
    }

    @Tool(description = "获取指定业务模块的 MCP 工具列表，返回每个工具的名称、描述和输入/输出 Schema。"
            + "可用于了解 AI 可直接调用的业务操作。先用 listModules 找到模块 id。")
    public String getModuleTools(
            @ToolParam(description = "模块 id，如 iam-user") String moduleId) {
        if (moduleService.findById(moduleId).isEmpty()) {
            return toJson(Map.of("error", "Module not found: " + moduleId,
                    "hint", "Use listModules to see all available module ids."));
        }
        List<ModuleDescriptor.McpToolDefinition> tools = moduleService.listTools(moduleId);
        return toJson(Map.of("moduleId", moduleId, "tools_count", tools.size(), "tools", tools));
    }

    // ---- helpers ----

    private Map<String, Object> toSummary(ModuleDescriptor m) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", m.id());
        map.put("name", m.name());
        map.put("domain", m.domain());
        map.put("status", m.status());
        map.put("description", m.description());
        map.put("gradle_ref", m.gradleRef());
        return map;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException ex) {
            return "{\"error\": \"serialization failed\"}";
        }
    }
}
