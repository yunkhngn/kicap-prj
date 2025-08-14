/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author yunkh
 */
public class Product {
    private Long id;
    private Long categoryId;
    private Long brandId;
    private String name;
    private String slug;
    private String sku;
    private String description;
    private BigDecimal basePrice;
    private BigDecimal salePrice;
    private String thumbnailUrl;
    private boolean active = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<ProductVariant> variants;
    private Category category;
    private Brand brand;

    public Product() {
    }

    public Product(Long id, Brand brand, Category category, List<ProductVariant> variants, LocalDateTime updatedAt, LocalDateTime createdAt, boolean active, String thumbnailUrl, BigDecimal salePrice, BigDecimal basePrice, String description, String sku, String slug, String name, Long brandId, Long categoryId) {
        this.id = id;
        this.brand = brand;
        this.category = category;
        this.variants = variants;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.active = active;
        this.thumbnailUrl = thumbnailUrl;
        this.salePrice = salePrice;
        this.basePrice = basePrice;
        this.description = description;
        this.sku = sku;
        this.slug = slug;
        this.name = name;
        this.brandId = brandId;
        this.categoryId = categoryId;
    }

    public Long getId() {
        return id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getSku() {
        return sku;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public Category getCategory() {
        return category;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "Product{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", brandId=" + brandId +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", sku='" + sku + '\'' +
                ", description='" + description + '\'' +
                ", basePrice=" + basePrice +
                ", salePrice=" + salePrice +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", active=" + active +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", variants=" + variants +
                ", category=" + category +
                ", brand=" + brand +
                '}';
    }
}
