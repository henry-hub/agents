package pub.ihub.agent.mcp.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * IHub 能力目录服务，负责加载和检索 catalog.json 数据。
 * <p>
 * catalog.json 路径通过 {@code ihub.catalog.path} 属性配置，
 * 默认指向 classpath 中打包的 catalog.json（供开发/测试），
 * 生产部署时可指向 libs 仓库实际文件路径。
 */
@Service
public class CatalogService {

    private static final Logger log = LoggerFactory.getLogger(CatalogService.class);

    @Value("${ihub.catalog.resource:classpath:ihub-catalog/catalog.json}")
    private Resource catalogResource;

    private final ObjectMapper objectMapper;
    private CatalogRoot catalog;

    public CatalogService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    void load() {
        if (!catalogResource.exists()) {
            log.warn("IHub catalog not found at {}, catalog tools will return empty results", catalogResource);
            catalog = new CatalogRoot(null, null, null, List.of());
            return;
        }
        try {
            catalog = objectMapper.readValue(catalogResource.getInputStream(), CatalogRoot.class);
            log.info("IHub catalog loaded: {} components from {}", catalog.components().size(), catalogResource);
        } catch (IOException e) {
            log.error("Failed to load IHub catalog: {}", e.getMessage(), e);
            catalog = new CatalogRoot(null, null, null, List.of());
        }
    }

    /** 列出所有 domain 名称（去重、排序） */
    public List<String> listDomains() {
        return catalog.components().stream()
                .map(CatalogEntry::domain)
                .distinct()
                .sorted()
                .toList();
    }

    /** 按 domain 过滤组件 */
    public List<CatalogEntry> listByDomain(String domain) {
        return catalog.components().stream()
                .filter(e -> domain.equals(e.domain()))
                .toList();
    }

    /** 按 id 精确查找 */
    public Optional<CatalogEntry> findById(String id) {
        return catalog.components().stream()
                .filter(e -> id.equals(e.id()))
                .findFirst();
    }

    /**
     * 全文搜索：在 id、name、description、use_case、tags 中匹配关键词（大小写不敏感）。
     * 多个关键词用空格分隔，所有词都需命中（AND 语义）。
     */
    public List<CatalogEntry> search(String query) {
        if (query == null || query.isBlank()) return catalog.components();
        String[] terms = query.toLowerCase().split("\\s+");
        return catalog.components().stream()
                .filter(e -> matchesAll(e, terms))
                .toList();
    }

    /** 获取组件总数 */
    public int count() {
        return catalog.components().size();
    }

    private boolean matchesAll(CatalogEntry e, String[] terms) {
        String haystack = buildSearchText(e);
        for (String term : terms) {
            if (!haystack.contains(term)) return false;
        }
        return true;
    }

    private String buildSearchText(CatalogEntry e) {
        return (nvl(e.id()) + " " + nvl(e.name()) + " " + nvl(e.nameEn()) + " "
                + nvl(e.domain()) + " " + nvl(e.description()) + " "
                + nvl(e.useCase()) + " "
                + (e.tags() != null ? String.join(" ", e.tags()) : ""))
                .toLowerCase();
    }

    private String nvl(String s) {
        return s == null ? "" : s;
    }
}
