package com.mailboom.api.application.admin.usecase;

import com.mailboom.api.application.admin.in.port.GetGeneralMetricsUseCase;
import com.mailboom.api.infrastructure.common.dto.GeneralMetricsDTO;
import com.mailboom.api.infrastructure.common.metricsservice.GetGeneralMetricsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GetGeneralMetricsUseCaseImpl implements GetGeneralMetricsUseCase {
    private final GetGeneralMetricsService service;

    @Override
    public GeneralMetricsDTO execute() {

        return service.getAccountGeneralMetrics();
    }
}
