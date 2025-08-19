/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;

/**
 *
 * @author yunkhngn
 */
public class CartItem {
    private Long productId;
    private Long variantId;    
    private String name;
    private String variantName; 
    private int qty;
    private BigDecimal price;   
    private String thumbnailUrl;

    public CartItem() {
    }

    public CartItem(Long productId, Long variantId, String name, String variantName,
                String thumbnailUrl, int qty, java.math.BigDecimal price) {
        this.productId = productId;
        this.variantId = variantId;
        this.name = name;
        this.variantName = variantName;
        this.thumbnailUrl = thumbnailUrl;
        this.qty = qty;
        this.price = price;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getVariantId() {
        return variantId;
    }

    public String getName() {
        return name;
    }

    public String getVariantName() {
        return variantName;
    }

    public int getQty() {
        return qty;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setVariantId(Long variantId) {
        this.variantId = variantId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
    
    
     public BigDecimal getLineTotal() {
        return price.multiply(java.math.BigDecimal.valueOf(qty));
    }
}
