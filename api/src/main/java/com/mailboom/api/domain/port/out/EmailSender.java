package com.mailboom.api.domain.port.out;

import com.mailboom.api.domain.model.campaign.Campaign;

public interface EmailSender {
    void send(Campaign campaign);
}
