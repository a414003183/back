package com.telecom.scm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

/**
 * SpringDoc OpenAPI 配置。
 *
 * <p>启动后访问：
 *
 * <ul>
 *   <li>Swagger UI：http://localhost:8080/swagger-ui.html
 *   <li>OpenAPI 文档：http://localhost:8080/v3/api-docs
 * </ul>
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("电信供应链管理系统 API")
                                .description("Telecom SCM Backend API 文档")
                                .version("0.0.1-SNAPSHOT")
                                .contact(new Contact().name("telecom-scm-team")));
    }
}
