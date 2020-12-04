<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
response.setHeader("Content-Type", "application/json;charset=UTF-8");//注意加上这一句
out.print("{\"httpCode\":499}");
%>
