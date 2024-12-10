package com.cheil.core.service.integrations.notifications;

public interface WhatsappService {
    void processAlerts(String recipient, String message);
}
