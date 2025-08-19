<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/partials/header.jspf" %>

<c:choose>
  <c:when test="${empty order}">
    <h2>Không tìm thấy đơn hàng</h2>
  </c:when>
  <c:otherwise>
    <h2>Đặt hàng thành công!</h2>
    <p>Mã đơn: <strong>${order.code}</strong></p>
    <p>Trạng thái: ${order.status}</p>
  </c:otherwise>
</c:choose>

<%@ include file="/partials/footer.jspf" %>