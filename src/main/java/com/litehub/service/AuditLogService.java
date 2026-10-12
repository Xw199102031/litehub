package com.litehub.service;

import com.litehub.model.AuditLog;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AuditLogService {

    @Transactional
    public void record(Long actorUserId, String action, String resourceType, Long resourceId,
                       String detailJson, String ipAddress, String userAgent) {
        AuditLog log = new AuditLog();
        log.actorUserId = actorUserId;
        log.action = action;
        log.resourceType = resourceType;
        log.resourceId = resourceId;
        log.detail = detailJson;
        log.ipAddress = ipAddress;
        log.userAgent = userAgent;
        log.persist();
    }
}
