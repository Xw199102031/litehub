package com.litehub.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "casbin_policy", uniqueConstraints = @UniqueConstraint(columnNames = {"subject","object","action"}))
public class CasbinPolicy extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String subject;

    @Column(nullable = false)
    public String object;

    @Column(nullable = false)
    public String action;

    @Column(name = "created_at")
    public java.time.Instant createdAt = java.time.Instant.now();
}
