package com.hunesion.gucamole;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GuacServletConfig {

    @Bean
    public GuacamoleController guacamoleController() {
        return new GuacamoleController();
    }
    @Bean
    public ServletRegistrationBean<GuacamoleController> guacTunnelServlet(GuacamoleController guacamoleController) {
        return new ServletRegistrationBean<>(
                guacamoleController,  // <-- Use Spring-managed bean
                "/tunnel",
                "/tunnel/*"
        );
    }
}

