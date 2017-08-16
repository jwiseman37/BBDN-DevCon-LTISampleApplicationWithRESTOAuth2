package com.mycompany.model;

import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: jwiseman
 * Date: Jul 19, 2017
 * Time: 10:04:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class LaunchModel {
    private String ltiParms;
    private String returnUrl;
    private String action;
    private String courseId;
    private String userId;
    private String restUrl;
    private String restAuthUrl;

    public ModelAndView getLaunchView () {
        Map<String, String> launchValues = new HashMap<String, String>();
        launchValues.put ("ltiParms", getLtiParms());
        launchValues.put ("returnUrl", getReturnUrl());
        launchValues.put ("courseId", getCourseId());
        launchValues.put ("userId", getUserId());
        launchValues.put ("action", getAction());
        launchValues.put ("restUrl", getRestUrl());
        launchValues.put ("restAuthUrl", getRestAuthUrl());
        return new  ModelAndView ("launch", launchValues);
    }

    public String getLtiParms() {
        return ltiParms;
    }

    public void setLtiParms(String ltiParms) {
        this.ltiParms = ltiParms;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRestUrl() {
        return restUrl;
    }

    public void setRestUrl(String restUrl) {
        this.restUrl = restUrl;
    }

    public String getRestAuthUrl() {
        return restAuthUrl;
    }

    public void setRestAuthUrl(String restAuthUrl) {
        this.restAuthUrl = restAuthUrl;
    }
}
