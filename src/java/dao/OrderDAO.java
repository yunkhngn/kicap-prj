package dao;

import model.Order;
import model.OrderItem;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO extends DBConnect {

    /* ========= Helpers ========= */

    private static LocalDateTime getLdt(ResultSet rs, String col) throws SQLException {
        LocalDateTime v = null;
        try { v = rs.getObject(col, LocalDateTime.class); }
        catch (AbstractMethodError | SQLFeatureNotSupportedException ignore) { /* fallback */ }
        if (v == null) {
            Timestamp ts = rs.getTimestamp(col);
            if (ts != null) v = ts.toLocalDateTime();
        }
        return v;
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getLong("id"));
        o.setCode(rs.getString("code"));
        o.setCustomerName(rs.getNString("customer_name"));
        o.setPhone(rs.getString("phone"));
        o.setAddress(rs.getNString("address"));
        o.setNote(rs.getNString("note"));
        o.setTotal(rs.getBigDecimal("total"));
        o.setStatus(Order.Status.valueOf(rs.getString("status")));
        o.setCreatedAt(getLdt(rs, "created_at"));
        return o;
    }

    private OrderItem mapItem(ResultSet rs) throws SQLException {
        OrderItem it = new OrderItem();
        it.setId(rs.getLong("id"));
        it.setOrderId(rs.getLong("order_id"));
        it.setProductId(rs.getLong("product_id"));
        long vId = rs.getLong("variant_id");
        it.setVariantId(rs.wasNull() ? null : vId);
        it.setQty(rs.getInt("qty"));
        it.setPrice(rs.getBigDecimal("price"));
        // nếu có join tên sp/biến thể:
        safeSet(rs, it, "product_name", true);
        safeSet(rs, it, "variant_name", true);
        return it;
    }

    private static void safeSet(ResultSet rs, OrderItem it, String col, boolean nvarchar) {
        try {
            String val = nvarchar ? rs.getNString(col) : rs.getString(col);
            if (val != null) {
                if ("product_name".equals(col)) it.setProductName(val);
                else if ("variant_name".equals(col)) it.setVariantName(val);
            }
        } catch (SQLException ignore) {}
    }

    /* ========= API ========= */

    /** Tạo đơn + items (transaction). Trả về orderId. */
    public long create(Order order, List<OrderItem> items) {
        String sqlOrder = """
          INSERT INTO Orders(code,customer_name,phone,address,note,total,status)
          VALUES (?,?,?,?,?,?,?)
        """;
        String sqlItem = """
          INSERT INTO OrderItems(order_id,product_id,variant_id,qty,price)
          VALUES (?,?,?,?,?)
        """;

        try (Connection cn = getConnection()) {
            cn.setAutoCommit(false);
            long orderId;

            try (PreparedStatement ps = cn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, order.getCode());
                ps.setNString(2, order.getCustomerName());
                ps.setString(3, order.getPhone());
                ps.setNString(4, order.getAddress());
                ps.setNString(5, order.getNote());
                ps.setBigDecimal(6, order.getTotal());
                ps.setString(7, order.getStatus().name());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("Không lấy được id đơn");
                    orderId = keys.getLong(1);
                }
            }

            try (PreparedStatement ps = cn.prepareStatement(sqlItem)) {
                for (OrderItem it : items) {
                    ps.setLong(1, orderId);
                    ps.setLong(2, it.getProductId());
                    if (it.getVariantId() == null) ps.setNull(3, Types.BIGINT);
                    else ps.setLong(3, it.getVariantId());
                    ps.setInt(4, it.getQty());
                    ps.setBigDecimal(5, it.getPrice());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            cn.commit();
            return orderId;
        } catch (Exception e) {
            throw new RuntimeException("OrderDAO.create (transaction)", e);
        }
    }

    /** Lấy đơn theo id, kèm danh sách items. */
    public Order findById(long id) {
        String sql = "SELECT * FROM Orders WHERE id=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order o = mapOrder(rs);
                    o.setItems(listItems(id, cn));
                    return o;
                }
            }
        } catch (SQLException e) { throw new RuntimeException("OrderDAO.findById", e); }
        return null;
    }

    /** Lấy đơn theo code, kèm items. */
    public Order findByCode(String code) {
        String sql = "SELECT * FROM Orders WHERE code=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order o = mapOrder(rs);
                    o.setItems(listItems(o.getId(), cn));
                    return o;
                }
            }
        } catch (SQLException e) { throw new RuntimeException("OrderDAO.findByCode", e); }
        return null;
    }

    /** Liệt kê items của đơn (cùng connection khi có). */
    public List<OrderItem> listItems(long orderId) {
        try (Connection cn = getConnection()) {
            return listItems(orderId, cn);
        } catch (SQLException e) { throw new RuntimeException("OrderDAO.listItems", e); }
    }
    private List<OrderItem> listItems(long orderId, Connection cn) throws SQLException {
        // Nếu muốn kèm tên sản phẩm/biến thể:
        String sql = """
          SELECT oi.*,
                 p.name  AS product_name,
                 v.name  AS variant_name
          FROM OrderItems oi
          LEFT JOIN Products p ON oi.product_id = p.id
          LEFT JOIN ProductVariants v ON oi.variant_id = v.id
          WHERE oi.order_id=?
          ORDER BY oi.id
        """;
        List<OrderItem> out = new ArrayList<>();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapItem(rs));
            }
        }
        return out;
    }

    /** Cập nhật trạng thái đơn. */
    public int updateStatus(long orderId, Order.Status newStatus) {
        String sql = "UPDATE Orders SET status=? WHERE id=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, newStatus.name());
            ps.setLong(2, orderId);
            return ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("OrderDAO.updateStatus", e); }
    }

    /** Danh sách đơn cho admin: lọc keyword(code/phone/name) + status, có phân trang. */
    public List<Order> list(String keyword, Order.Status status, int page, int size) {
        String base = "SELECT * FROM Orders WHERE 1=1";
        List<Object> params = new ArrayList<>();
        StringBuilder sb = new StringBuilder(base);

        if (keyword != null && !keyword.isBlank()) {
            sb.append(" AND (code LIKE ? OR phone LIKE ? OR customer_name LIKE ?)");
            String kw = "%" + keyword + "%";
            params.add(kw); params.add(kw); params.add(kw);
        }
        if (status != null) {
            sb.append(" AND status=?");
            params.add(status.name());
        }
        sb.append(" ORDER BY created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add(Math.max(0, (page - 1) * size));
        params.add(size);

        List<Order> list = new ArrayList<>();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof String s) ps.setString(i + 1, s);
                else if (p instanceof Integer ii) ps.setInt(i + 1, ii);
                else ps.setObject(i + 1, p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapOrder(rs));
            }
        } catch (SQLException e) { throw new RuntimeException("OrderDAO.list", e); }
        return list;
    }

    /** Đếm tổng số đơn (phục vụ phân trang). */
    public int count(String keyword, Order.Status status) {
        String base = "SELECT COUNT(*) FROM Orders WHERE 1=1";
        List<Object> params = new ArrayList<>();
        StringBuilder sb = new StringBuilder(base);

        if (keyword != null && !keyword.isBlank()) {
            sb.append(" AND (code LIKE ? OR phone LIKE ? OR customer_name LIKE ?)");
            String kw = "%" + keyword + "%";
            params.add(kw); params.add(kw); params.add(kw);
        }
        if (status != null) {
            sb.append(" AND status=?");
            params.add(status.name());
        }

        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof String s) ps.setString(i + 1, s);
                else ps.setObject(i + 1, p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                rs.next(); return rs.getInt(1);
            }
        } catch (SQLException e) { throw new RuntimeException("OrderDAO.count", e); }
    }

    /** Tạo mã đơn ngắn dạng KC-YYYYMMDD-xxxx */
    public static String genOrderCode() {
        String date = java.time.LocalDate.now().toString().replace("-", "");
        int rnd = (int)(Math.random() * 9000) + 1000;
        return "KC-" + date + "-" + rnd;
    }

    /* ========= Test nhanh ========= */
    public static void main(String[] args) {
        OrderDAO dao = new OrderDAO();

        // List 5 đơn mới nhất
        List<Order> recent = dao.list(null, null, 1, 5);
        System.out.println("Recent orders: " + recent.size());
        for (Order o : recent) {
            System.out.printf("%s | %s | %s | total=%s | status=%s%n",
                    o.getCode(), o.getCustomerName(), o.getPhone(), o.getTotal(), o.getStatus());
        }

        // Tìm theo code (đổi code thực tế trong DB để test)
        if (!recent.isEmpty()) {
            String code = recent.get(0).getCode();
            Order full = dao.findByCode(code);
            System.out.println("\nOrder " + code + " items = " + (full.getItems() == null ? 0 : full.getItems().size()));
        }
    }
}