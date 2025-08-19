<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/partials/header.jspf" %>

<h2>Đăng nhập</h2>

<c:if test="${not empty error}">
  <div class="error">${error}</div>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/login" class="form">
  <input type="hidden" name="returnUrl" value="${param.returnUrl}"/>
  <label>Username</label>
  <input name="username" required/>
  <label>Password</label>
  <input name="password" type="password" required/>
  <button type="submit">Đăng nhập</button>
</form>

<%@ include file="/partials/footer.jspf" %>