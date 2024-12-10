package com.cheil.qa.utils;

import lombok.Getter;

@Getter
public enum ServerStatus {
    FAILED("Failed"), COMPLETED("Completed"), RUNNING("Running"), STOPPED("Stopped"), SCHEDULED("Scheduled"), PENDING("Pending"), UNDEFINED("Undefined");
    public final String status;

    ServerStatus(String status) {
        this.status = status;
    }
}
