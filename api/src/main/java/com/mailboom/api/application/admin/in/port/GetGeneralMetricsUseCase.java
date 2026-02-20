package com.mailboom.api.application.admin.in.port;

import com.mailboom.api.infrastructure.common.dto.GeneralMetricsDTO;

public interface GetGeneralMetricsUseCase {
    GeneralMetricsDTO execute();
}
