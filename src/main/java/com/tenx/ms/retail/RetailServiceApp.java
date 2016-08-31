package com.tenx.ms.retail;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.SimpleCommandLinePropertySource;

/**
 * Spring Boot Application Inititalization class for retail MicroService
 */
@ComponentScan(basePackages = {"com.tenx.ms.commons", "com.tenx.ms.retail"})
@EnableDiscoveryClient
@SpringBootApplication
@SuppressWarnings("PMD.UseUtilityClass")
public class RetailServiceApp {

    /**
     * Gets a default profile if no active profiles are configured.
     */
    private static String[] getDefaultProfile(String[] args) {
        SimpleCommandLinePropertySource properties = new SimpleCommandLinePropertySource(args);
        if (!properties.containsProperty("spring.profiles.active") && System.getenv("SPRING_PROFILES_ACTIVE") == null) {
            return new String[] { "dev" };
        }
        return new String[0];
    }

    /**
     * Main method used when the application is run via spring boot
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder(RetailServiceApp.class)
                .bannerMode(Banner.Mode.OFF)
                .profiles(getDefaultProfile(args))
                .run(args);
    }
}
