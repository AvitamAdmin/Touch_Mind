package com.cheil.qa.service;

import com.aventstack.extentreports.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
@NoArgsConstructor
public class ActionResult {
    private Status stepStatus;
    private ObjectId qaTestResultId;
}
