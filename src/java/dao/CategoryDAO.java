package dao;

import java.sql.*;
import java.util.*;
import model.Category;

public class CategoryDAO extends DBConnect {

    private Category map(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setId(rs.getLong("id"));
        c.setName(rs.getNString("name"));
        c.setSlug(rs.getString("slug"));
        return c;
    }

    public List<Category> all() {
        String sql = "SELECT id,name,slug FROM Categories ORDER BY name";
        List<Category> out = new ArrayList<>();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        } catch (SQLException e) { throw new RuntimeException("CategoryDAO.all", e); }
        return out;
    }

    public Category findBySlug(String slug) {
        String sql = "SELECT id,name,slug FROM Categories WHERE slug=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, slug);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { throw new RuntimeException("CategoryDAO.findBySlug", e); }
        return null;
    }
}
