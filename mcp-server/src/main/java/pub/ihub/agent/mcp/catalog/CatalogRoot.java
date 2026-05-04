package pub.ihub.agent.mcp.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * catalog.json 顶层结构，由 merge-catalog.py 生成。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CatalogRoot(
        @JsonProperty("catalog_version") String version,
        String generated,
        Map<String, Object> taxonomy,
        List<CatalogEntry> components
) {}
