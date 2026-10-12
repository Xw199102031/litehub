package com.litehub.auth;

import com.litehub.model.CasbinPolicy;
import com.litehub.repository.CasbinPolicyRepository;
import com.litehub.service.AuditLogService;
import org.casbin.jcasbin.main.Enforcer;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class CasbinService {

    private Enforcer enforcer;

    @ConfigProperty(name = "casbin.policy.path", defaultValue = "config/casbin/policy.csv")
    String policyPath;

    @Inject
    CasbinPolicyRepository policyRepository;

    @Inject
    AuditLogService auditLogService;

    @PostConstruct
    void init() {
        try {
            // model from resources
            var modelResource = Thread.currentThread().getContextClassLoader().getResource("casbin/model.conf");
            String modelPath = modelResource != null ? modelResource.getPath() : "";

            // initialize enforcer with model and an empty policy; we'll load from DB
            enforcer = new Enforcer(modelPath);

            // load policies from DB and add to enforcer
            List<CasbinPolicy> policies = policyRepository.listAll();
            for (CasbinPolicy p : policies) {
                enforcer.addPolicy(p.subject, p.object, p.action);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Casbin enforcer", e);
        }
    }

    public boolean can(String subject, String obj, String act) {
        return enforcer.enforce(subject, obj, act);
    }

    public boolean addPolicy(String subject, String obj, String act, Long actorUserId, String ip, String ua) {
        boolean added = enforcer.addPolicy(subject, obj, act);
        if (added) {
            // persist to DB
            CasbinPolicy existing = policyRepository.findByTriple(subject, obj, act);
            if (existing == null) {
                CasbinPolicy cp = new CasbinPolicy();
                cp.subject = subject;
                cp.object = obj;
                cp.action = act;
                policyRepository.persist(cp);
            }
            auditLogService.record(actorUserId, "ADD_POLICY", "CASBIN_POLICY", null,
                    String.format("{\"subject\":\"%s\",\"object\":\"%s\",\"action\":\"%s\"}", subject, obj, act), ip, ua);
        }
        return added;
    }

    public boolean removePolicy(String subject, String obj, String act, Long actorUserId, String ip, String ua) {
        boolean removed = enforcer.removePolicy(subject, obj, act);
        if (removed) {
            CasbinPolicy existing = policyRepository.findByTriple(subject, obj, act);
            if (existing != null) policyRepository.delete(existing);
            auditLogService.record(actorUserId, "REMOVE_POLICY", "CASBIN_POLICY", null,
                    String.format("{\"subject\":\"%s\",\"object\":\"%s\",\"action\":\"%s\"}", subject, obj, act), ip, ua);
        }
        return removed;
    }

    public List<List<String>> listPolicies() {
        return enforcer.getPolicy();
    }

    public void reload() {
        enforcer.clearPolicy();
        List<CasbinPolicy> policies = policyRepository.listAll();
        for (CasbinPolicy p : policies) {
            enforcer.addPolicy(p.subject, p.object, p.action);
        }
    }
}
