package model;

import java.math.BigDecimal;

public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private Long variantId;
    private int qty;
    private BigDecimal price;

    private String productName;
    private String variantName;

    public OrderItem() {
    }

    public OrderItem(Long id, Long orderId, Long productId, Long variantId, int qty, BigDecimal price, String productName, String variantName) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.variantId = variantId;
        this.qty = qty;
        this.price = price;
        this.productName = productName;
        this.variantName = variantName;
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getVariantId() {
        return variantId;
    }

    public int getQty() {
        return qty;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getProductName() {
        return productName;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setVariantId(Long variantId) {
        this.variantId = variantId;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", productId=" + productId +
                ", variantId=" + variantId +
                ", qty=" + qty +
                ", price=" + price +
                ", productName='" + productName + '\'' +
                ", variantName='" + variantName + '\'' +
                '}';
    }
}
