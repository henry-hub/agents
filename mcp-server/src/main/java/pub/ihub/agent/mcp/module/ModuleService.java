package pub.ihub.agent.mcp.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 业务模块注册服务，扫描 module-descriptor.json 并缓存为 {@link ModuleDescriptor}。
 * <p>
 * 默认扫描 classpath 中 {@code META-INF/ihub/module-descriptor.json}（每个业务模块 JAR 携带一份）。
 * 可通过 {@code ihub.modules.resource} 配置覆盖为文件系统 glob，便于开发时直接加载
 * modules 仓库的描述符，无需将业务模块 JAR（及其 Spring auto-config）加入 classpath。
 * <p>
 * 示例：{@code ihub.modules.resource=file:///path/to/modules/ihub-module-iam/src/main/resources/META-INF/ihub/module-descriptor.json}
 */
@Service
public class ModuleService {

    private static final Logger log = LoggerFactory.getLogger(ModuleService.class);

    private final ObjectMapper objectMapper;

    @Value("${ihub.modules.resource:classpath*:META-INF/ihub/module-descriptor.json}")
    private String descriptorPattern;

    private List<ModuleDescriptor> modules = List.of();

    public ModuleService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    void load() {
        List<ModuleDescriptor> loaded = new ArrayList<>();
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                    .getResources(descriptorPattern);
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
        log.info("IHub module registry loaded: {} modules from {}", modules.size(), descriptorPattern);
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
