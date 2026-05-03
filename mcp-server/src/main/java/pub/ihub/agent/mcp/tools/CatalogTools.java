package pub.ihub.agent.mcp.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class CatalogTools {

    @Tool(description = "列出 IHub 能力目录的所有领域")
    public String listDomains() {
        // P2: 从 libs catalog.json 动态加载
        return """
            {
              "domains": [
                "infrastructure", "data", "ddd", "mapping", "security",
                "distributed", "workflow", "observability", "documentation",
                "testing", "messaging", "utilities", "meta"
              ]
            }""";
    }

    @Tool(description = "搜索 IHub 能力目录中的组件")
    public String searchCatalog(@ToolParam(description = "搜索关键词") String query) {
        // P2: 实现全文搜索
        return "{\"results\": []}";
    }
}
