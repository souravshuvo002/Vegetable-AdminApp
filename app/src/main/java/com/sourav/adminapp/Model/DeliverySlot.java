package com.sourav.adminapp.Model;

public class DeliverySlot {

    private String id, day, start_time, end_time, status, allocation;
    private String max_day, order_time, delivery_charge, delivery_free_amount;

    public DeliverySlot() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAllocation() {
        return allocation;
    }

    public void setAllocation(String allocation) {
        this.allocation = allocation;
    }

    public String getMax_day() {
        return max_day;
    }

    public void setMax_day(String max_day) {
        this.max_day = max_day;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public String getDelivery_charge() {
        return delivery_charge;
    }

    public void setDelivery_charge(String delivery_charge) {
        this.delivery_charge = delivery_charge;
    }

    public String getDelivery_free_amount() {
        return delivery_free_amount;
    }

    public void setDelivery_free_amount(String delivery_free_amount) {
        this.delivery_free_amount = delivery_free_amount;
    }
}
