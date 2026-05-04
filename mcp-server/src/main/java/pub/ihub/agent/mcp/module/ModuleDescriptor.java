package pub.ihub.agent.mcp.module;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * 业务模块描述符（MCP Server 侧的反序列化模型）。
 * <p>
 * 对应 modules 仓库 {@code META-INF/ihub/module-descriptor.json} 格式，
 * 以及 {@code module-descriptor-v1.json} JSON Schema 定义。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ModuleDescriptor(
        String id,
        String name,
        @JsonProperty("name_en") String nameEn,
        String version,
        String domain,
        String description,
        @JsonProperty("ai_context") String aiContext,
        List<ModuleCapability> capabilities,
        List<Map<String, Object>> dependencies,
        Map<String, String> maven,
        @JsonProperty("gradle_ref") String gradleRef,
        @JsonProperty("config_prefix") String configPrefix,
        List<String> tags,
        String status,
        @JsonProperty("mcp_tools") List<McpToolDefinition> mcpTools
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ModuleCapability(
            String id,
            String name,
            String type,
            String description,
            @JsonProperty("input_schema") Map<String, Object> inputSchema,
            @JsonProperty("output_schema") Map<String, Object> outputSchema
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record McpToolDefinition(
            String name,
            String description,
            @JsonProperty("input_schema") Map<String, Object> inputSchema,
            @JsonProperty("output_schema") Map<String, Object> outputSchema
    ) {}
}
