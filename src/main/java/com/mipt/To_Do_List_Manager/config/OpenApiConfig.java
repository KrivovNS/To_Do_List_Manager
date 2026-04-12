package com.mipt.To_Do_List_Manager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI toDoListOpenApi(
            @Value("${app.api.title}") String title,
            @Value("${app.api.version}") String version,
            @Value("${app.api.description}") String description,
            @Value("${app.api.contact.name}") String contactName,
            @Value("${app.api.contact.email}") String contactEmail,
            @Value("${app.api.contact.url}") String contactUrl
    ) {
        return new OpenAPI().info(
                new Info()
                        .title(title)
                        .version(version)
                        .description(description)
                        .contact(new Contact()
                                .name(contactName)
                                .email(contactEmail)
                                .url(contactUrl))
        );
    }
}
