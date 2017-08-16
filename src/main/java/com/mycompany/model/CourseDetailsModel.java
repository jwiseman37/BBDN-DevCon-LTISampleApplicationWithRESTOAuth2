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
public class CourseDetailsModel {
    private String returnUrl;
    private String action;
    private String courseName;
    private String courseDescription;
    private String studentId;
    private String code;

    public ModelAndView getCourseDetailsView () {
        Map<String, String> courseDetailsValues = new HashMap<String, String>();
        courseDetailsValues.put ("returnUrl", getReturnUrl());
        courseDetailsValues.put ("studentId", getStudentId());
        courseDetailsValues.put ("action", getAction());
        courseDetailsValues.put ("courseName", getCourseName());
        courseDetailsValues.put ("courseDescription", getCourseDescription());
        courseDetailsValues.put ("code", getCode());
        return new  ModelAndView ("course_details", courseDetailsValues);
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

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
