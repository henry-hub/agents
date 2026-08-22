package pub.ihub.agent.mcp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Bearer 令牌认证过滤器（ADR-0004 最小安全基线）。
 * <p>
 * 配置 {@code ihub.mcp.security.token} 后，所有请求（健康检查除外）
 * 必须携带 {@code Authorization: Bearer <token>}，否则返回 401。
 * 未配置令牌时为开发模式：放行并以 WARN 级别提示。
 */
@Configuration
@EnableConfigurationProperties(McpSecurityProperties.class)
public class TokenAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(TokenAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    @Bean
    OncePerRequestFilter mcpTokenFilter(McpSecurityProperties properties) {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                String path = request.getRequestURI();
                if (path.startsWith("/actuator/health")) {
                    filterChain.doFilter(request, response);
                    return;
                }
                if (!properties.tokenEnabled()) {
                    log.warn("IHub MCP Server running WITHOUT authentication "
                            + "(ihub.mcp.security.token not set) — dev mode only!");
                    filterChain.doFilter(request, response);
                    return;
                }
                String auth = request.getHeader("Authorization");
                if (auth != null && auth.startsWith(BEARER_PREFIX)
                        && properties.token().equals(auth.substring(BEARER_PREFIX.length()).trim())) {
                    filterChain.doFilter(request, response);
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"unauthorized\",\"hint\":"
                            + "\"Provide Authorization: Bearer <ihub.mcp.security.token>\"}");
                }
            }
        };
    }
}
