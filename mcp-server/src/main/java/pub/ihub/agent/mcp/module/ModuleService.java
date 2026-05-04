package pub.ihub.agent.mcp.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 业务模块注册服务，扫描 classpath 中所有
 * {@code META-INF/ihub/module-descriptor.json} 并缓存为 {@link ModuleDescriptor}。
 * <p>
 * 通常每个业务模块 JAR 携带一份描述符；MCP Server 启动时自动发现并加载全部。
 */
@Service
public class ModuleService {

    private static final Logger log = LoggerFactory.getLogger(ModuleService.class);
    private static final String DESCRIPTOR_PATTERN = "classpath*:META-INF/ihub/module-descriptor.json";

    private final ObjectMapper objectMapper;
    private List<ModuleDescriptor> modules = List.of();

    public ModuleService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    void load() {
        List<ModuleDescriptor> loaded = new ArrayList<>();
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                    .getResources(DESCRIPTOR_PATTERN);
            for (Resource r : resources) {
                try {
                    ModuleDescriptor descriptor = objectMapper.readValue(r.getInputStream(), ModuleDescriptor.class);
                    loaded.add(descriptor);
                    log.debug("Loaded module descriptor: {} from {}", descriptor.id(), r);
                } catch (IOException e) {
                    log.warn("Failed to parse module descriptor at {}: {}", r, e.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("Failed to scan module descriptors: {}", e.getMessage(), e);
        }
        this.modules = List.copyOf(loaded);
        log.info("IHub module registry loaded: {} modules", modules.size());
    }

    /** 列出所有已注册模块（摘要） */
    public List<ModuleDescriptor> listAll() {
        return modules;
    }

    /** 按 id 精确查找模块 */
    public Optional<ModuleDescriptor> findById(String id) {
        return modules.stream().filter(m -> id.equals(m.id())).findFirst();
    }

    /** 按 domain 过滤模块 */
    public List<ModuleDescriptor> findByDomain(String domain) {
        return modules.stream().filter(m -> domain.equals(m.domain())).toList();
    }

    /** 列出指定模块的 MCP 工具定义 */
    public List<ModuleDescriptor.McpToolDefinition> listTools(String moduleId) {
        return findById(moduleId)
                .map(m -> m.mcpTools() != null ? m.mcpTools() : List.<ModuleDescriptor.McpToolDefinition>of())
                .orElse(List.of());
    }
}
