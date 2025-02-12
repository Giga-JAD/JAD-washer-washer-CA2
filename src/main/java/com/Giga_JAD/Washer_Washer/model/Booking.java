package com.Giga_JAD.Washer_Washer.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "bookings")  // Assuming your table is named "bookings"
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bookingId;
    
    @Column(name = "status_id")  // If your column name is different, adjust accordingly
    private int statusId;
    
    @Column(name = "service_name")
    private String serviceName;
    
    @Column(name = "scheduled_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date scheduledTime;
    
    // Getters and Setters remain the same
    public int getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }
    
    public int getStatusId() {
        return statusId;
    }
    
    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public Date getScheduledTime() {
        return scheduledTime;
    }
    
    public void setScheduledTime(Date scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
}