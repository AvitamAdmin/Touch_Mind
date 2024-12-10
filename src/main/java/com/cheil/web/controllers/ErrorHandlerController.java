package com.cheil.web.controllers;


import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


@RestController
public class ErrorHandlerController implements ErrorController {

    Logger LOG = LoggerFactory.getLogger(ErrorHandlerController.class);

    @RequestMapping("/error")
    @ResponseBody
    public ModelAndView getErrorPath(HttpServletRequest httpRequest) {
        Integer errorcode = getErrorCode(httpRequest);
        ModelAndView modelAndView = new ModelAndView("error");
        Exception e = (Exception) httpRequest.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        LOG.error("Error code " + errorcode);
        LOG.error("Error message  " + e);
        modelAndView.addObject("errorCode", errorcode);
        modelAndView.addObject("message", e);
        return modelAndView;
    }

    private int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest
                .getAttribute("jakarta.servlet.error.status_code");
    }
}