package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order {
    public enum Status { PENDING, CONFIRMED, SHIPPING, COMPLETED, CANCELED }

    private Long id;
    private String code;
    private String customerName;
    private String phone;
    private String address;
    private String note;
    private BigDecimal total;
    private Status status = Status.PENDING;
    private LocalDateTime createdAt;

    private List<OrderItem> items;

    public Order() {
    }

    public Order(Long id, String code, String customerName, String phone, String address, String note, BigDecimal total, model.Order.Status status, LocalDateTime createdAt, List<OrderItem> items) {
        this.id = id;
        this.code = code;
        this.customerName = customerName;
        this.phone = phone;
        this.address = address;
        this.note = note;
        this.total = total;
        this.status = status;
        this.createdAt = createdAt;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getNote() {
        return note;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public model.Order.Status getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public void setStatus(model.Order.Status status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "Order{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", customerName='" + customerName + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", note='" + note + '\'' +
                ", total=" + total +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", items=" + items +
                '}';
    }
}
