package com.telecom.scm.config;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class DbRuntimeGuard implements ApplicationRunner {

    private final Environment environment;
    private final boolean requireDbProfile;

    public DbRuntimeGuard(
            Environment environment,
            @Value("${app.runtime.require-db-profile:true}") boolean requireDbProfile) {
        this.environment = environment;
        this.requireDbProfile = requireDbProfile;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!requireDbProfile) {
            return;
        }

        Set<String> activeProfiles =
                new LinkedHashSet<>(Arrays.asList(environment.getActiveProfiles()));
        if (activeProfiles.contains("db")) {
            return;
        }

        throw new IllegalStateException(
                "The backend must run with the 'db' Spring profile. Active profiles: "
                        + activeProfiles);
    }
}
