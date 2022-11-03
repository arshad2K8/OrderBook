package com.code.orderbook.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.HashSet;

@Configuration
public class SwaggerConfig {
    private String appName;
    private String description;

    public SwaggerConfig(@Value("${app.info.name}") String appName, @Value("${app.info.description}") String description) {
        this.appName = appName;
        this.description = description;
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.code.orderbook.controller"))
                .paths(PathSelectors.any())
                .build()
                .pathMapping("/")
                .produces(new HashSet<>(Arrays.asList(
                        "application/json",
                        "text/plain; charset=utf-8"
                )))
                .apiInfo(apiinfo())
                ;
    }

    private ApiInfo apiinfo() {
        return new ApiInfoBuilder()
                .title(appName)
                .description(description)
                .build();
    }
}
