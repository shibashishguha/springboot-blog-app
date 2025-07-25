package com.blog.app.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfig {

    private Dotenv dotenv;

    @PostConstruct
    public void init() {
        dotenv = Dotenv.configure()
                       .ignoreIfMissing() 
                       .load();

        System.setProperty("spring.datasource.url", dotenv.get("SPRING_DATASOURCE_URL"));
        System.setProperty("spring.datasource.username", dotenv.get("SPRING_DATASOURCE_USERNAME"));
        System.setProperty("spring.datasource.password", dotenv.get("SPRING_DATASOURCE_PASSWORD"));
        System.setProperty("spring.jpa.hibernate.ddl-auto", dotenv.get("SPRING_JPA_HIBERNATE_DDL_AUTO"));
        System.setProperty("spring.jpa.show-sql", dotenv.get("SPRING_JPA_SHOW_SQL"));
        System.setProperty("jwt.secret", dotenv.get("JWT_SECRET"));
        System.setProperty("jwt.expiration", dotenv.get("JWT_EXPIRATION"));
        System.setProperty("spring.mail.host", dotenv.get("SPRING_MAIL_HOST"));
        System.setProperty("spring.mail.port", dotenv.get("SPRING_MAIL_PORT"));
        System.setProperty("spring.mail.username", dotenv.get("SPRING_MAIL_USERNAME"));
        System.setProperty("spring.mail.password", dotenv.get("SPRING_MAIL_PASSWORD"));
        System.setProperty("spring.mail.properties.mail.smtp.auth", dotenv.get("SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH"));
        System.setProperty("spring.mail.properties.mail.smtp.starttls.enable", dotenv.get("SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE"));
        System.setProperty("spring.mail.properties.mail.smtp.starttls.required", dotenv.get("SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_REQUIRED"));
        System.setProperty("spring.mail.properties.mail.smtp.connectiontimeout", dotenv.get("SPRING_MAIL_PROPERTIES_MAIL_SMTP_CONNECTIONTIMEOUT"));
        System.setProperty("spring.mail.properties.mail.smtp.timeout", dotenv.get("SPRING_MAIL_PROPERTIES_MAIL_SMTP_TIMEOUT"));
        System.setProperty("spring.mail.properties.mail.smtp.writetimeout", dotenv.get("SPRING_MAIL_PROPERTIES_MAIL_SMTP_WRITETIMEOUT"));
    }
}