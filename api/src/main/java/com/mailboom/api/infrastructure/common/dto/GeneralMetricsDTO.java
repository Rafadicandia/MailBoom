package com.mailboom.api.infrastructure.common.dto;

public record GeneralMetricsDTO(
        long totalDelivered,
        long totalBounces,
        long totalComplaints,
        long totalRejects
) {
}
