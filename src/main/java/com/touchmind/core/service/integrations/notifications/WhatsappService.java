package com.touchmind.core.service.integrations.notifications;

public interface WhatsappService {
    void processAlerts(String recipient, String message);
}
