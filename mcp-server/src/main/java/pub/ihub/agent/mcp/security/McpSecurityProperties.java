package pub.ihub.agent.mcp.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MCP Server 安全配置（ADR-0004）。
 *
 * @param token  Bearer 静态令牌；配置后所有请求必须携带
 *               {@code Authorization: Bearer <token>}，未配置则为开发模式（放行 + WARN 日志）
 */
@ConfigurationProperties(prefix = "ihub.mcp.security")
public record McpSecurityProperties(String token) {

    /** 是否启用令牌认证（未配置令牌时为开发模式） */
    public boolean tokenEnabled() {
        return token != null && !token.isBlank();
    }
}
