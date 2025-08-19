<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/partials/header.jspf" %>

<h2>Thanh toán</h2>
<form method="post" action="${pageContext.request.contextPath}/checkout" class="form">
  <label>Họ tên</label>
  <input name="name" required/>
  <label>Điện thoại</label>
  <input name="phone" required/>
  <label>Địa chỉ</label>
  <textarea name="address" required></textarea>
  <label>Ghi chú</label>
  <textarea name="note"></textarea>
  <button type="submit">Đặt hàng</button>
</form>

<%@ include file="/partials/footer.jspf" %>