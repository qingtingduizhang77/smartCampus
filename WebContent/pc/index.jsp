<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>pc index</title>
</head>
<body>

 </br>orgId:${orgId}
 </br>orgName:${orgName}
 <form action="<%=request.getContextPath() %>/admin_back/j_spring_security_check" method="post">
 <table>
 <tr><td>帐号：</td><td><input type="text" name="j_username" id="" value="admin"/></td></tr>
  <tr><td>密码：</td><td><input type="text" name="j_password" id="" value="password"/></td></tr>
   <tr>
   <td><input type="submit" value="登录"/></td>
   <td></td></tr>
 </table>
 </form>
</body>
</html>