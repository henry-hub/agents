package pub.ihub.agent.mcp.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Bearer 令牌认证集成测试（ADR-0004）。
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "ihub.mcp.security.token=test-secret-token")
class McpSecurityIT {

    private static final HttpClient HTTP = HttpClient.newHttpClient();

    @LocalServerPort
    private int port;

    private HttpResponse<String> get(String bearerToken) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(
                URI.create("http://localhost:" + port + "/mcp/message")).GET();
        if (bearerToken != null) {
            builder.header("Authorization", "Bearer " + bearerToken);
        }
        return HTTP.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void requestWithoutTokenIsRejected() throws Exception {
        assertEquals(401, get(null).statusCode());
    }

    @Test
    void requestWithWrongTokenIsRejected() throws Exception {
        assertEquals(401, get("wrong-token").statusCode());
    }

    @Test
    void requestWithValidTokenPassesAuthentication() throws Exception {
        // 通过认证后具体状态码取决于 MCP 端点处理（404/405/200 均可），但不应是 401
        assertNotEquals(401, get("test-secret-token").statusCode());
    }
}
