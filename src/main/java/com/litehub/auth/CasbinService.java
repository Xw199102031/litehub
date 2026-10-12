package com.litehub.auth;

import org.casbin.jcasbin.main.Enforcer;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@ApplicationScoped
public class CasbinService {

    private Enforcer enforcer;

    @ConfigProperty(name = "casbin.policy.path", defaultValue = "config/casbin/policy.csv")
    String policyPath;

    @PostConstruct
    void init() {
        try {
            // ensure policy file exists; if not, copy default from resources
            Path p = Path.of(policyPath);
            if (!Files.exists(p)) {
                Files.createDirectories(p.getParent());
                // copy default policy from resources if present
                var is = Thread.currentThread().getContextClassLoader().getResourceAsStream("casbin/policy.csv");
                if (is != null) {
                    Files.copy(is, p);
                } else {
                    Files.createFile(p);
                }
            }
            // model config path in resources
            String modelPath = "src/main/resources/casbin/model.conf";
            // If running from jar, resource path won't exist on filesystem; Enforcer accepts classpath paths not always—use model resource
            var modelResource = Thread.currentThread().getContextClassLoader().getResource("casbin/model.conf");
            if (modelResource != null) {
                modelPath = modelResource.getPath();
            }

            enforcer = new Enforcer(modelPath, p.toAbsolutePath().toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Casbin enforcer", e);
        }
    }

    public boolean can(String subject, String obj, String act) {
        return enforcer.enforce(subject, obj, act);
    }

    public boolean addPolicy(String subject, String obj, String act) {
        boolean added = enforcer.addPolicy(subject, obj, act);
        if (added) enforcer.savePolicy();
        return added;
    }

    public boolean removePolicy(String subject, String obj, String act) {
        boolean removed = enforcer.removePolicy(subject, obj, act);
        if (removed) enforcer.savePolicy();
        return removed;
    }

    public List<List<String>> listPolicies() {
        return enforcer.getPolicy();
    }

    public void reload() {
        enforcer.loadPolicy();
    }
}
