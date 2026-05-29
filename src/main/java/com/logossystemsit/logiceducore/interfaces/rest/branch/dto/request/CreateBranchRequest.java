package com.logossystemsit.logiceducore.interfaces.rest.branch.dto.request;

public record CreateBranchRequest(
        String name,
        String code,
        String shortName,
        String description,
        String email,
        String phone,
        String address
) {}
