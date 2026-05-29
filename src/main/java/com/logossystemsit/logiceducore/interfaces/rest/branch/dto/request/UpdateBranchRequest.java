package com.logossystemsit.logiceducore.interfaces.rest.branch.dto.request;

public record UpdateBranchRequest(
        String name,
        String code,
        String shortName,
        String description,
        String email,
        String phone,
        String address
) {}
