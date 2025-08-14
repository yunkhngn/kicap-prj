package dao;

import model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends DBConnect {

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

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFullName(rs.getNString("full_name"));
        u.setRole(User.Role.valueOf(rs.getString("role")));
        u.setActive(rs.getBoolean("active"));
        u.setCreatedAt(getLdt(rs, "created_at"));
        return u;
    }

    /* ========= CRUD cơ bản ========= */

    /** Tạo user mới. Trả về id vừa tạo. (passwordHash phải truyền sẵn) */
    public long create(String username, String passwordHash, String fullName, User.Role role, boolean active) {
        String sql = """
            INSERT INTO Users(username, password_hash, full_name, role, active)
            VALUES (?,?,?,?,?)
        """;
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setNString(3, fullName);
            ps.setString(4, role == null ? User.Role.CUSTOMER.name() : role.name());
            ps.setBoolean(5, active);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
            // SQL Server đôi khi cần SELECT SCOPE_IDENTITY() — nhưng RETURN_GENERATED_KEYS thường đủ
            throw new SQLException("Không lấy được ID vừa tạo");
        } catch (SQLException e) {
            throw new RuntimeException("UserDAO.create", e);
        }
    }

    public User findById(long id) {
        String sql = "SELECT * FROM Users WHERE id=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) { throw new RuntimeException("UserDAO.findById", e); }
    }

    public User findByUsername(String username) {
        String sql = "SELECT TOP 1 * FROM Users WHERE username=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) { throw new RuntimeException("UserDAO.findByUsername", e); }
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM Users WHERE username=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new RuntimeException("UserDAO.existsByUsername", e); }
    }

    /** Đổi mật khẩu (hash mới) */
    public int updatePassword(long id, String newHash) {
        String sql = "UPDATE Users SET password_hash=? WHERE id=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setLong(2, id);
            return ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("UserDAO.updatePassword", e); }
    }

    /** Cập nhật tên hiển thị */
    public int updateProfile(long id, String fullName) {
        String sql = "UPDATE Users SET full_name=? WHERE id=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setNString(1, fullName);
            ps.setLong(2, id);
            return ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("UserDAO.updateProfile", e); }
    }

    /** Bật/tắt active */
    public int setActive(long id, boolean active) {
        String sql = "UPDATE Users SET active=? WHERE id=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setLong(2, id);
            return ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("UserDAO.setActive", e); }
    }

    /** Đổi role, có ghi audit nếu bảng UserRoleAudit tồn tại (không bắt buộc) */
    public int updateRole(long targetUserId, User.Role newRole, long actorUserId) {
        String getOldSql = "SELECT role FROM Users WHERE id=?";
        String updSql = "UPDATE Users SET role=? WHERE id=?";
        try (Connection cn = getConnection()) {
            cn.setAutoCommit(false);
            String oldRole = null;

            try (PreparedStatement ps = cn.prepareStatement(getOldSql)) {
                ps.setLong(1, targetUserId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) oldRole = rs.getString(1);
                }
            }
            if (oldRole == null) { cn.rollback(); return 0; }

            int n;
            try (PreparedStatement ps = cn.prepareStatement(updSql)) {
                ps.setString(1, newRole.name());
                ps.setLong(2, targetUserId);
                n = ps.executeUpdate();
            }

            // Audit (best-effort)
            String auditSql = """
              IF OBJECT_ID('dbo.UserRoleAudit','U') IS NOT NULL
              INSERT INTO UserRoleAudit(actor_id,target_id,old_role,new_role)
              VALUES (?,?,?,?)
            """;
            try (PreparedStatement ps = cn.prepareStatement(auditSql)) {
                ps.setLong(1, actorUserId);
                ps.setLong(2, targetUserId);
                ps.setString(3, oldRole);
                ps.setString(4, newRole.name());
                ps.executeUpdate();
            } catch (SQLException ignore) { /* bảng audit không có cũng ok */ }

            cn.commit();
            return n;
        } catch (SQLException e) { throw new RuntimeException("UserDAO.updateRole", e); }
    }

    /* ========= Danh sách (lọc + phân trang) ========= */

    /**
     * Liệt kê user có lọc keyword (username/full_name chứa keyword) và role.
     * page bắt đầu từ 1.
     */
    public List<User> list(String keyword, User.Role role, int page, int size) {
        String base = "SELECT * FROM Users WHERE 1=1";
        List<Object> params = new ArrayList<>();
        StringBuilder sb = new StringBuilder(base);

        if (keyword != null && !keyword.isBlank()) {
            sb.append(" AND (username LIKE ? OR full_name LIKE ?)");
            String kw = "%" + keyword + "%";
            params.add(kw); params.add(kw);
        }
        if (role != null) {
            sb.append(" AND role = ?");
            params.add(role.name());
        }
        sb.append(" ORDER BY created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        int offset = Math.max(0, (page - 1) * size);
        params.add(offset);
        params.add(size);

        List<User> out = new ArrayList<>();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof String s) ps.setString(i + 1, s);
                else if (p instanceof Integer ii) ps.setInt(i + 1, ii);
                else ps.setObject(i + 1, p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException e) { throw new RuntimeException("UserDAO.list", e); }
        return out;
    }

    /* ========= Login helper (trả về user để tự check password ngoài DAO) ========= */

    public User loginLookup(String username) {
        // lấy user active để controller so sánh mật khẩu (BCrypt) phía ngoài
        String sql = "SELECT TOP 1 * FROM Users WHERE username=? AND active=1";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) { throw new RuntimeException("UserDAO.loginLookup", e); }
    }

    /* ========= Test nhanh ========= */
    public static void main(String[] args) {
        UserDAO dao = new UserDAO();

        // 1) Tìm theo username
        User u = dao.findByUsername("admin"); // đổi username thực tế trong DB
        System.out.println("findByUsername(admin) -> " + u);

        // 2) Liệt kê 10 user đầu tiên
        List<User> list = dao.list("", null, 1, 10);
        System.out.println("list size = " + list.size());
        for (User x : list) {
            System.out.printf("ID=%d | %s | %s | role=%s | active=%s%n",
                    x.getId(), x.getUsername(), x.getFullName(), x.getRole(), x.isActive());
        }
    }
}