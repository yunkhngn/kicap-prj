package dao;

import model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO extends DBConnect {

    // Đổi lại tên bảng nếu DB của bạn là "Category" thay vì "Categories"
    private static final String TABLE = "Categories";

    /* ===== Helpers ===== */
    private Category map(ResultSet rs) throws SQLException {
        return new Category(
                rs.getLong("id"),
                rs.getNString("name"),
                rs.getString("slug")
        );
    }

    /* ===== CRUD ===== */
    public List<Category> getAll() {
        String sql = "SELECT id, name, slug FROM " + TABLE + " ORDER BY name";
        List<Category> list = new ArrayList<>();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("CategoryDAO.getAll", e);
        }
        return list;
    }

    public Category getById(Long id) {
        String sql = "SELECT id, name, slug FROM " + TABLE + " WHERE id = ?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("CategoryDAO.getById", e);
        }
    }

    public Category getBySlug(String slug) {
        String sql = "SELECT id, name, slug FROM " + TABLE + " WHERE slug = ?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, slug);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("CategoryDAO.getBySlug", e);
        }
    }

    public boolean insert(Category c) {
        String sql = "INSERT INTO " + TABLE + " (name, slug) VALUES (?, ?)";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setNString(1, c.getName());
            ps.setString(2, c.getSlug());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("CategoryDAO.insert", e);
        }
    }

    public boolean update(Category c) {
        String sql = "UPDATE " + TABLE + " SET name = ?, slug = ? WHERE id = ?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setNString(1, c.getName());
            ps.setString(2, c.getSlug());
            ps.setLong(3, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("CategoryDAO.update", e);
        }
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM " + TABLE + " WHERE id = ?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("CategoryDAO.delete", e);
        }
    }

    /* ===== Test nhanh ===== */
    public static void main(String[] args) {
        CategoryDAO dao = new CategoryDAO();
        System.out.println("All categories:");
        for (Category c : dao.getAll()) {
            System.out.println(c);
        }
    }
}