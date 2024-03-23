package org.goormuniv.ponnect.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        Server server = new Server();
        server.setUrl("https://www.ponnect.kro.kr");
        List<Server> servers = new ArrayList<>();
        servers.add(server);
        return new OpenAPI()
                .components(jwtToken())
                .addSecurityItem(new SecurityRequirement().addList("authorization"))
                .servers(servers)
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Ponnect")
                .description("Ponnect의 API 명세서")
                .version("1.0.0");
    }

    private Components jwtToken () {
        return new Components()
                .addSecuritySchemes("authorization", new SecurityScheme()
                        .name("authorization")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));
    }
}
