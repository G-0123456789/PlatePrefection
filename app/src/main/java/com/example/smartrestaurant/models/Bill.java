package com.example.smartrestaurant.models;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Bill implements Serializable {
    public enum Status {
        PENDING,
        PAID,
        CANCELLED,
        REFUNDED
    }

    public enum PaymentMethod {
        CASH,
        CREDIT_CARD,
        DEBIT_CARD,
        MOBILE_PAYMENT,
        ONLINE_TRANSFER,
        GIFT_CARD
    }

    private String billId;
    private String orderId;
    private String tableNumber;
    private double subTotal;
    private double taxAmount;
    private double tipAmount;
    private double totalAmount;
    private Status status;
    private PaymentMethod paymentMethod;
    private Date createdAt;
    private Date paidAt;
    private String waiterId;
    private String customerName;
    private String notes;

    // Default constructor required for Firebase
    public Bill() {
        this.billId = UUID.randomUUID().toString();
        this.createdAt = new Date();
        this.status = Status.PENDING;
    }

    public Bill(String orderId, String tableNumber, double subTotal, double taxAmount,
                double tipAmount, String waiterId, String customerName, String notes) {
        this.billId = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.tableNumber = tableNumber;
        this.subTotal = subTotal;
        this.taxAmount = taxAmount;
        this.tipAmount = tipAmount;
        this.totalAmount = subTotal + taxAmount + tipAmount;
        this.waiterId = waiterId;
        this.customerName = customerName;
        this.notes = notes;
        this.createdAt = new Date();
        this.status = Status.PENDING;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
        recalculateTotal();
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
        recalculateTotal();
    }

    public double getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(double tipAmount) {
        this.tipAmount = tipAmount;
        recalculateTotal();
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    private void recalculateTotal() {
        this.totalAmount = this.subTotal + this.taxAmount + this.tipAmount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        if (status == Status.PAID && paidAt == null) {
            this.paidAt = new Date();
        }
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Date paidAt) {
        this.paidAt = paidAt;
    }

    public String getWaiterId() {
        return waiterId;
    }

    public void setWaiterId(String waiterId) {
        this.waiterId = waiterId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void processPayment(PaymentMethod method) {
        this.paymentMethod = method;
        this.status = Status.PAID;
        this.paidAt = new Date();
    }
}