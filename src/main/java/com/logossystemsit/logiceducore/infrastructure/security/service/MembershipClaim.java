package com.logossystemsit.logiceducore.infrastructure.security.service;

public record MembershipClaim(String role, String scopeType, String scopeRefId) {
}
