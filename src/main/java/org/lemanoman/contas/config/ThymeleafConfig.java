package org.lemanoman.contas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import java.nio.charset.StandardCharsets;

@Configuration
public class ThymeleafConfig {

    @Bean
    public SpringTemplateEngine springTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(htmlTemplateResolver());
        templateEngine.addDialect(new SpringSecurityDialect());
        return templateEngine;
    }

    @Bean
    public SpringResourceTemplateResolver htmlTemplateResolver() {
        final SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCacheable(false);
        resolver.setCheckExistence(false);
        return resolver;
    }
}
