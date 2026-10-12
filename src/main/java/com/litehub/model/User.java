package com.litehub.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true)
    public String username;

    @Column(name = "password_hash", nullable = false)
    public String password_hash;

    @Column(nullable = false, unique = true)
    public String email;

    @Column(nullable = false)
    public String role;

    @Column(name = "created_at")
    public java.time.Instant createdAt = java.time.Instant.now();
}