package com.fahdkhan.aicontrolplane.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerUiController {

    @GetMapping("/swagger/user")
    public String userSwaggerUi() {
        return "redirect:/swagger-ui/index.html?url=/v3/api-docs/user-api";
    }

    @GetMapping("/swagger/admin")
    public String adminSwaggerUi() {
        return "redirect:/swagger-ui/index.html?url=/v3/api-docs/admin-api";
    }
}
