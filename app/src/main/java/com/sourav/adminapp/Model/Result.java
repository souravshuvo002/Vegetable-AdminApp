package com.sourav.adminapp.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {

    @SerializedName("error")
    private Boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("user")
    private User user;

    @SerializedName("banner")
    private List<Banner> banners;

    @SerializedName("menu")
    private List<Category> menuList;

    @SerializedName("foods")
    private List<Foods> foodsList;

    @SerializedName("orders")
    private List<Order> orderList;

    @SerializedName("orderDetails")
    private List<Order> orderDetails;

    @SerializedName("orderItems")
    private List<Order> orderItems;

    @SerializedName("token")
    private Token token;

    @SerializedName("shipper")
    private List<User> allShippers;

    @SerializedName("transferData")
    private List<User> allWalletShippers;

    @SerializedName("area")
    private List<Area> areaList;

    @SerializedName("unit")
    private List<Unit> unitList;

    @SerializedName("coupons")
    private List<Coupon> couponList;

    @SerializedName("slots")
    private List<DeliverySlot> deliverySlotList;

    @SerializedName("slotssss")
    private List<DeliverySlot> slotList;

    @SerializedName("users")
    private List<User> allUserList;

    @SerializedName("summary")
    private Order saleSummary;

    @SerializedName("orderedFoodQuantity")
    private List<Order> orderedFoodQuantity;

    @SerializedName("allreviews")
    private List<Review> allReviewList;

    @SerializedName("textScroll")
    private TextScroll textScroll;

    public String message_id;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Banner> getBanners() {
        return banners;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
    }

    public List<Category> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<Category> menuList) {
        this.menuList = menuList;
    }

    public List<Foods> getFoodsList() {
        return foodsList;
    }

    public void setFoodsList(List<Foods> foodsList) {
        this.foodsList = foodsList;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public List<Order> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<Order> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public List<Order> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<Order> orderItems) {
        this.orderItems = orderItems;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public List<User> getAllShippers() {
        return allShippers;
    }

    public void setAllShippers(List<User> allShippers) {
        this.allShippers = allShippers;
    }

    public List<Area> getAreaList() {
        return areaList;
    }

    public void setAreaList(List<Area> areaList) {
        this.areaList = areaList;
    }

    public List<Unit> getUnitList() {
        return unitList;
    }

    public void setUnitList(List<Unit> unitList) {
        this.unitList = unitList;
    }

    public List<Coupon> getCouponList() {
        return couponList;
    }

    public void setCouponList(List<Coupon> couponList) {
        this.couponList = couponList;
    }

    public List<DeliverySlot> getDeliverySlotList() {
        return deliverySlotList;
    }

    public void setDeliverySlotList(List<DeliverySlot> deliverySlotList) {
        this.deliverySlotList = deliverySlotList;
    }

    public List<DeliverySlot> getSlotList() {
        return slotList;
    }

    public void setSlotList(List<DeliverySlot> slotList) {
        this.slotList = slotList;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public List<User> getAllUserList() {
        return allUserList;
    }

    public void setAllUserList(List<User> allUserList) {
        this.allUserList = allUserList;
    }

    public List<User> getAllWalletShippers() {
        return allWalletShippers;
    }

    public void setAllWalletShippers(List<User> allWalletShippers) {
        this.allWalletShippers = allWalletShippers;
    }

    public Order getSaleSummary() {
        return saleSummary;
    }

    public void setSaleSummary(Order saleSummary) {
        this.saleSummary = saleSummary;
    }

    public List<Order> getOrderedFoodQuantity() {
        return orderedFoodQuantity;
    }

    public void setOrderedFoodQuantity(List<Order> orderedFoodQuantity) {
        this.orderedFoodQuantity = orderedFoodQuantity;
    }

    public List<Review> getAllReviewList() {
        return allReviewList;
    }

    public void setAllReviewList(List<Review> allReviewList) {
        this.allReviewList = allReviewList;
    }

    public TextScroll getTextScroll() {
        return textScroll;
    }

    public void setTextScroll(TextScroll textScroll) {
        this.textScroll = textScroll;
    }
}