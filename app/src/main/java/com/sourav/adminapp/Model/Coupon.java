package com.sourav.adminapp.Model;

public class Coupon {

    private String id, name, code, type, discount, discount_limit, total, start_date, end_date, uses_total, uses_customer, status, date_added;
    private String id_coupon, id_user, id_order, amount;
    private String coupon_id, code_uses_total, code_customer_uses_total;


    public Coupon() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getUses_total() {
        return uses_total;
    }

    public void setUses_total(String uses_total) {
        this.uses_total = uses_total;
    }

    public String getUses_customer() {
        return uses_customer;
    }

    public void setUses_customer(String uses_customer) {
        this.uses_customer = uses_customer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getId_coupon() {
        return id_coupon;
    }

    public void setId_coupon(String id_coupon) {
        this.id_coupon = id_coupon;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getId_order() {
        return id_order;
    }

    public void setId_order(String id_order) {
        this.id_order = id_order;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(String coupon_id) {
        this.coupon_id = coupon_id;
    }

    public String getCode_uses_total() {
        return code_uses_total;
    }

    public void setCode_uses_total(String code_uses_total) {
        this.code_uses_total = code_uses_total;
    }

    public String getCode_customer_uses_total() {
        return code_customer_uses_total;
    }

    public void setCode_customer_uses_total(String code_customer_uses_total) {
        this.code_customer_uses_total = code_customer_uses_total;
    }

    public String getDiscount_limit() {
        return discount_limit;
    }

    public void setDiscount_limit(String discount_limit) {
        this.discount_limit = discount_limit;
    }
}
