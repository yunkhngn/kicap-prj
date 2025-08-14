package dao;

import model.Product;
import model.ProductVariant;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO extends DBConnect {

    /* ===== Helpers ===== */

    private static LocalDateTime getLdt(ResultSet rs, String col) throws SQLException {
        // MSSQL + JDBC cho phép lấy LDT trực tiếp với JDK 17
        LocalDateTime v = null;
        try { v = rs.getObject(col, LocalDateTime.class); }
        catch (AbstractMethodError | SQLFeatureNotSupportedException ignore) { /* fallback */ }
        if (v == null) {
            Timestamp ts = rs.getTimestamp(col);
            if (ts != null) v = ts.toLocalDateTime();
        }
        return v;
    }

    private Product mapProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setCategoryId(rs.getLong("category_id"));

        long brandId = rs.getLong("brand_id");
        p.setBrandId(rs.wasNull() ? null : brandId);

        p.setName(rs.getNString("name"));
        p.setSlug(rs.getString("slug"));
        p.setSku(rs.getString("sku"));
        p.setDescription(rs.getNString("description"));

        p.setBasePrice(rs.getBigDecimal("base_price"));
        BigDecimal sp = rs.getBigDecimal("sale_price");
        p.setSalePrice(rs.wasNull() ? null : sp);

        p.setThumbnailUrl(rs.getString("thumbnail_url"));
        p.setActive(rs.getBoolean("active"));

        p.setCreatedAt(getLdt(rs, "created_at"));
        p.setUpdatedAt(getLdt(rs, "updated_at"));
        return p;
    }

    private ProductVariant mapVariant(ResultSet rs) throws SQLException {
        ProductVariant v = new ProductVariant();
        v.setId(rs.getLong("id"));
        v.setProductId(rs.getLong("product_id"));
        v.setName(rs.getNString("name"));
        v.setPrice(rs.getBigDecimal("price"));
        v.setStock(rs.getInt("stock"));
        return v;
    }

    /* ===== Queries chính ===== */

    /** Sản phẩm mới nhất (active=1) */
    public List<Product> listNewest(int limit) {
        String sql = "SELECT TOP (?) * FROM Products WHERE active=1 ORDER BY created_at DESC";
        List<Product> out = new ArrayList<>();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapProduct(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("ProductDAO.listNewest", e);
        }
        return out;
    }

    /** Đếm SP theo category slug (để phân trang) */
    public int countByCategorySlug(String slug) {
        String sql = """
          SELECT COUNT(*) FROM Products p
          JOIN Categories c ON p.category_id = c.id
          WHERE c.slug = ? AND p.active = 1
        """;
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, slug);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1); }
        } catch (SQLException e) {
            throw new RuntimeException("ProductDAO.countByCategorySlug", e);
        }
    }

    /** Danh sách SP theo category slug + phân trang + sort đơn giản */
    public List<Product> listByCategorySlug(String slug, int page, int size, String sort) {
        String orderBy = switch (sort == null ? "new" : sort) {
            case "price_asc"  -> "ISNULL(sale_price, base_price) ASC";
            case "price_desc" -> "ISNULL(sale_price, base_price) DESC";
            default -> "created_at DESC";
        };
        String sql = ("""
          SELECT p.* FROM Products p
          JOIN Categories c ON p.category_id = c.id
          WHERE c.slug = ? AND p.active = 1
          ORDER BY %s
          OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
        """).formatted(orderBy);

        List<Product> out = new ArrayList<>();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, slug);
            ps.setInt(2, Math.max(0, (page - 1) * size));
            ps.setInt(3, size);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapProduct(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("ProductDAO.listByCategorySlug", e);
        }
        return out;
    }

    /** Lấy SP theo slug + kèm variants */
    public Product findBySlug(String slug) {
        String sql = "SELECT * FROM Products WHERE slug = ?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, slug);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Product p = mapProduct(rs);
                    p.setVariants(listVariants(p.getId(), cn)); // dùng cùng connection
                    return p;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ProductDAO.findBySlug", e);
        }
        return null;
    }

    /** Variants của 1 product (overload dùng conn sẵn để tiết kiệm) */
    public List<ProductVariant> listVariants(long productId) {
        try (Connection cn = getConnection()) {
            return listVariants(productId, cn);
        } catch (SQLException e) {
            throw new RuntimeException("ProductDAO.listVariants", e);
        }
    }
    private List<ProductVariant> listVariants(long productId, Connection cn) throws SQLException {
        String sql = "SELECT * FROM ProductVariants WHERE product_id = ? ORDER BY id";
        List<ProductVariant> out = new ArrayList<>();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapVariant(rs));
            }
        }
        return out;
    }

    /** Gợi ý sản phẩm liên quan cùng category (trừ chính nó) */
    public List<Product> listRelated(long categoryId, long excludeProductId, int limit) {
        String sql = """
          SELECT TOP (?) * FROM Products
          WHERE active=1 AND category_id=? AND id <> ?
          ORDER BY created_at DESC
        """;
        List<Product> out = new ArrayList<>();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setLong(2, categoryId);
            ps.setLong(3, excludeProductId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapProduct(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("ProductDAO.listRelated", e);
        }
        return out;
    }
    public static void main(String[] args) {
    ProductDAO dao = new ProductDAO();

    // Test lấy 5 sản phẩm mới nhất
    List<Product> newest = dao.listNewest(5);
    System.out.println("=== Danh sách sản phẩm mới nhất ===");
    for (Product p : newest) {
        System.out.printf("ID: %d | Name: %s | Giá: %s%n",
                p.getId(),
                p.getName(),
                p.getSalePrice() != null ? p.getSalePrice() : p.getBasePrice());
    }

    // Test tìm theo slug
    String testSlug = "ten-slug-san-pham"; // thay slug thực tế
    Product product = dao.findBySlug(testSlug);
    if (product != null) {
        System.out.println("\n=== Thông tin sản phẩm ===");
        System.out.println(product);
    } else {
        System.out.println("\nKhông tìm thấy sản phẩm với slug: " + testSlug);
    }
}
}