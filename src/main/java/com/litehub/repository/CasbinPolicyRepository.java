package com.litehub.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import com.litehub.model.CasbinPolicy;

@ApplicationScoped
public class CasbinPolicyRepository implements PanacheRepository<CasbinPolicy> {
    public CasbinPolicy findByTriple(String subject, String object, String action) {
        return find("subject = ?1 and object = ?2 and action = ?3", subject, object, action).firstResult();
    }
}
