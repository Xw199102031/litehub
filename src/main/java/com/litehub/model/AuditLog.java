package com.litehub.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "audit_logs")
public class AuditLog extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "actor_user_id")
    public Long actorUserId;

    @Column(name = "action", nullable = false)
    public String action;

    @Column(name = "resource_type", nullable = false)
    public String resourceType;

    @Column(name = "resource_id")
    public Long resourceId;

    @Column(name = "detail", columnDefinition = "jsonb")
    public String detail;

    @Column(name = "ip_address")
    public String ipAddress;

    @Column(name = "user_agent")
    public String userAgent;

    @Column(name = "created_at")
    public java.time.Instant createdAt = java.time.Instant.now();
}
