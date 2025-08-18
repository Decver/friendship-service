package com.socialnetwork.friendship.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI friendshipOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Friendship Service API")
                        .description("Микросервис для управления дружбой: заявки, друзья, отклонения и т.д.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Tima Kek")
                                .url("https://github.com/Decver")
                                .email("your@email.com")));
    }
}
