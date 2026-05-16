package com.example.EMS.EmployeeConfiguration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // For images
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");

        // For PDFs
        registry.addResourceHandler("/uploadsPdf/**")
                .addResourceLocations("file:uploadsPdf/");
        
        registry.addResourceHandler("/onBoardingProfiles/**")
        .addResourceLocations("file:onBoardingProfiles/");
        
        registry.addResourceHandler("/onBoardingProfilesPdf/**")
        .addResourceLocations("file:onBoardingProfilesPdf/");
    }
}