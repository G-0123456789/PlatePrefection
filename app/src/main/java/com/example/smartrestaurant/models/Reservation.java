package com.example.smartrestaurant.models;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Reservation implements Serializable {
    public enum Status {
        PENDING,
        CONFIRMED,
        CHECKED_IN,
        COMPLETED,
        CANCELLED,
        NO_SHOW
    }

    private String reservationId;
    private String customerId;
    private String customerName;
    private String contactNumber;
    private String tableNumber;
    private int partySize;
    private Date reservationDate;
    private Date createdAt;
    private Date updatedAt;
    private Status status;
    private String notes;

    // Default constructor required for Firebase
    public Reservation() {
        this.reservationId = UUID.randomUUID().toString();
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.status = Status.PENDING;
    }

    public Reservation(String customerId, String customerName, String contactNumber,
                       String tableNumber, int partySize, Date reservationDate, String notes) {
        this.reservationId = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.customerName = customerName;
        this.contactNumber = contactNumber;
        this.tableNumber = tableNumber;
        this.partySize = partySize;
        this.reservationDate = reservationDate;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.status = Status.PENDING;
        this.notes = notes;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getPartySize() {
        return partySize;
    }

    public void setPartySize(int partySize) {
        this.partySize = partySize;
    }

    public Date getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(Date reservationDate) {
        this.reservationDate = reservationDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.updatedAt = new Date();
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}