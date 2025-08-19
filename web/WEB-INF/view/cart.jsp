<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/partials/header.jspf" %>

<h2>Giỏ hàng</h2>

<c:choose>
  <c:when test="${empty sessionScope.cart}">
    <p>Giỏ hàng trống. <a href="${pageContext.request.contextPath}/">Mua sắm ngay</a></p>
  </c:when>

  <c:otherwise>
    <table class="table">
      <thead>
      <tr>
        <th>Sản phẩm</th>
        <th>Biến thể</th>
        <th>SL</th>
        <th>Giá</th>
        <th>Tạm tính</th>
        <th></th>
      </tr>
      </thead>

      <tbody>
      <c:set var="sum" value="0" scope="page"/>
      <c:forEach items="${sessionScope.cart}" var="ci">
        <tr>
          <td>${ci.name}</td>
          <td><c:out value="${ci.variantName}" default="-"/></td>
          <td>
            <form method="post" action="${pageContext.request.contextPath}/cart">
              <input type="hidden" name="action" value="update"/>
              <input type="hidden" name="pid" value="${ci.productId}"/>
              <c:if test="${ci.variantId != null}">
                <input type="hidden" name="vid" value="${ci.variantId}"/>
              </c:if>

              <input type="number"
                     name="qty"
                     min="0"
                     value="${ci.qty != null ? ci.qty : 0}"
                     style="width:70px"/>
              <button type="submit">Cập nhật</button>
            </form>
          </td>
          <td>${ci.price}</td>
          <td>${ci.lineTotal}</td>
          <td>
            <c:url var="rmUrl" value="/cart">
              <c:param name="action" value="remove"/>
              <c:param name="pid" value="${ci.productId}"/>
              <c:if test="${ci.variantId != null}">
                <c:param name="vid" value="${ci.variantId}"/>
              </c:if>
            </c:url>
            <a href="${pageContext.request.contextPath}${rmUrl}"
               onclick="return confirm('Xóa sản phẩm này?');">Xóa</a>
          </td>
        </tr>
        <c:set var="sum" value="${sum + ci.lineTotal}" scope="page"/>
      </c:forEach>
      </tbody>

      <tfoot>
      <tr>
        <td colspan="4" style="text-align:right">Tổng:</td>
        <td colspan="2"><strong>${sum}</strong></td>
      </tr>
      </tfoot>
    </table>

    <p>
      <a class="btn" href="${pageContext.request.contextPath}/checkout">Thanh toán</a>
      <a class="link" href="${pageContext.request.contextPath}/">Tiếp tục mua hàng</a>
    </p>
  </c:otherwise>
</c:choose>

<%@ include file="/partials/footer.jspf" %>