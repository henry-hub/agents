package pub.ihub.agent.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class IHubMcpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(IHubMcpServerApplication.class, args);
    }

    /**
     * Spring Boot 4 默认使用 Jackson 3（tools.jackson）自动配置，
     * 当 classpath 仅有 Jackson 2（com.fasterxml）时需手动提供 ObjectMapper。
     */
    @Bean
    @ConditionalOnMissingBean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
