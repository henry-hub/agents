package pub.ihub.agent.mcp.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * IHub 能力目录单个组件条目，对应 libs/gradle/ihub-catalog/domains/*.json 中的条目结构。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CatalogEntry(
        String id,
        String name,
        @JsonProperty("name_en") String nameEn,
        String domain,
        String type,
        String description,
        @JsonProperty("use_case") String useCase,
        @JsonProperty("ai_context") String aiContext,
        List<String> alternatives,
        @JsonProperty("gradle_ref") Object gradleRef,
        @JsonProperty("version_ref") String versionRef,
        Object dependencies,
        List<String> tags,
        String status,
        List<String> stage,
        @JsonProperty("related_plugins") List<String> relatedPlugins,
        @JsonProperty("doc_url") String docUrl,
        @JsonProperty("ihub_layer") String ihubLayer
) {
    /** gradle_ref 可以是 String 或 List<String>，统一返回列表 */
    public List<String> gradleRefs() {
        if (gradleRef == null) return List.of();
        if (gradleRef instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return List.of(gradleRef.toString());
    }
}
