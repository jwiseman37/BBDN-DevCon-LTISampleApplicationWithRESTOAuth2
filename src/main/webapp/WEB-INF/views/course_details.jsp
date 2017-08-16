<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib uri="/bbNG" prefix="bbNG" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="pageTitle" value="Sample Tool Provider - Course Details"/>
<c:set var="pageInstructions" value=""/>

<bbNG:learningSystemPage title="${pageTitle}">

    <bbNG:pageHeader instructions="${pageInstructions}">
        <bbNG:pageTitleBar title="${pageTitle}"/>
    </bbNG:pageHeader>

    <b>Authorization Code:&nbsp;</b>
    ${code} 
    <br><br>
    
    <b>Course Name:&nbsp;</b>
    ${courseName} 
    <br><br>
    
    <b>Course Description:&nbsp;</b>
    ${courseDescription} 
    <br><br>
       
    <b>Student ID:&nbsp;</b>
    ${studentId} 
    <br><br>
    
    <bbNG:button label="Close Window" url="javascript:window.close();"/>
    <br><br>

</bbNG:learningSystemPage>

