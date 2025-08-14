package dao;

import model.Brand;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BrandDAO extends DBConnect {

    private static final String TABLE = "Brands";

    /* ===== Helpers ===== */
    private Brand map(ResultSet rs) throws SQLException {
        return new Brand(
                rs.getLong("id"),
                rs.getNString("name")
        );
    }

    /* ===== CRUD ===== */
    public List<Brand> getAll() {
        String sql = "SELECT id, name FROM " + TABLE + " ORDER BY name";
        List<Brand> list = new ArrayList<>();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("BrandDAO.getAll", e);
        }
        return list;
    }

    public Brand getById(Long id) {
        String sql = "SELECT id, name FROM " + TABLE + " WHERE id = ?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("BrandDAO.getById", e);
        }
    }

    public Brand getByName(String name) {
        String sql = "SELECT id, name FROM " + TABLE + " WHERE name = ?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setNString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("BrandDAO.getByName", e);
        }
    }

    public boolean insert(Brand b) {
        String sql = "INSERT INTO " + TABLE + " (name) VALUES (?)";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setNString(1, b.getName());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("BrandDAO.insert", e);
        }
    }

    public boolean update(Brand b) {
        String sql = "UPDATE " + TABLE + " SET name = ? WHERE id = ?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setNString(1, b.getName());
            ps.setLong(2, b.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("BrandDAO.update", e);
        }
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM " + TABLE + " WHERE id = ?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("BrandDAO.delete", e);
        }
    }

    /* ===== Test nhanh ===== */
    public static void main(String[] args) {
        BrandDAO dao = new BrandDAO();
        System.out.println("All brands:");
        for (Brand b : dao.getAll()) System.out.println(b);
    }
}