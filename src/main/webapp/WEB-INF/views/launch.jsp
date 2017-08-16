<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib uri="/bbNG" prefix="bbNG" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="pageTitle" value="Sample Tool Provider"/>
<c:set var="pageInstructions" value=""/>

<bbNG:learningSystemPage title="${pageTitle}">

    <bbNG:pageHeader instructions="${pageInstructions}">
        <bbNG:pageTitleBar title="${pageTitle}"/>
    </bbNG:pageHeader>

    <b>Custom Action:&nbsp;</b>
    ${action}
    <br><br>

    <b>Course ID:&nbsp;</b>
    ${courseId}
    <br><br>
   
    <b>User ID:&nbsp;</b>
    ${userId} 
    <br><br>
    
    <b>LTI Parameters:</b><br><br>
    <c:out escapeXml="false" value="${ltiParms}"/>
    <br><br>

    <bbNG:button label="Course Details with REST" url="${restUrl}"/>
    &nbsp;&nbsp;
    <bbNG:button label="Course Details with User Authorized REST" url="${restAuthUrl}"/>
    <br><br>    

    <c:if test="${returnUrl != 'NONE'}">
        <bbNG:button label="Return to Course" url="${returnUrl}"/>
        <br><br>
    </c:if>
    <c:if test="${returnUrl == 'NONE' || returnUrl == ''}">
        <bbNG:button label="Close Window" url="javascript:window.close();"/>
        <br><br>
    </c:if>

</bbNG:learningSystemPage>

