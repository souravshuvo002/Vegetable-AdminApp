package com.sourav.adminapp.Api;


import com.sourav.adminapp.Model.Result;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    //Update Text Scroll
    @FormUrlEncoded
    @POST("updateTextScroll")
    Call<Result> updateTextScroll(
            @Field("id") String id,
            @Field("text") String text,
            @Field("status") String status);

    //get Text scroll
    @GET("getTextScroll")
    Call<Result> getTextScroll();

    //get all banners
    @GET("getBanner")
    Call<Result> getBanners();

    //get all Users
    @GET("getAllUser")
    Call<Result> getAllUser();

    // register call
    @FormUrlEncoded
    @POST("registerUser")
    Call<Result> registerUser(
            @Field("username") String username,
            @Field("password") String password,
            @Field("phone") String phone,
            @Field("email") String email,
            @Field("address") String address);


    // login call
    @FormUrlEncoded
    @POST("loginUser")
    Call<Result> loginUser(
            @Field("phone") String phone,
            @Field("password") String password);

    //updating token for User
    @FormUrlEncoded
    @POST("updateUserToken")
    Call<Result> updateUserToken(
            @Field("id") String id,
            @Field("token") String token);

    // Uploading Banner details
    @Multipart
    @POST("addBanner")
    Call<Result> addBanner(
            @Part("name") RequestBody name,
            @Part MultipartBody.Part file);

    // Update Banner
    @Multipart
    @POST("updateBanner")
    Call<Result> updateBanner(
            @Part("id") RequestBody id,
            @Part("name") RequestBody name,
            @Part MultipartBody.Part file);

    //deleting banner
    @DELETE("deleteBanner/{id}")
    Call<Result> deleteBanner(
            @Path("id") String id);

    // Uploading Menu details
    @Multipart
    @POST("addMenu")
    Call<Result> addMenu(
            @Part("name") RequestBody name,
            @Part MultipartBody.Part file);

    // Update Menu
    @Multipart
    @POST("updateMenuImage")
    Call<Result> updateMenuImage(
            @Part("id") RequestBody id,
            @Part("name") RequestBody name,
            @Part MultipartBody.Part file);

    // Update Menu
    @FormUrlEncoded
    @POST("updateMenu/{id}")
    Call<Result> updateMenu(@Path("id") String id,
                            @Field("name") String name);

    //deleting Menu
    @DELETE("deleteMenu/{id}")
    Call<Result> deleteMenu(
            @Path("id") String id);

    //get all menu
    @GET("getMenu")
    Call<Result> getMenu();

    // Uploading Foods details
    @Multipart
    @POST("addFoodItem")
    Call<Result> addFoodItem(
            @Part("name") RequestBody name,
            @Part("price") RequestBody price,
            @Part("discount_price") RequestBody discount_price,
            @Part("description") RequestBody description,
            @Part("min_unit_amount") RequestBody min_unit_amount,
            @Part("unit") RequestBody unit,
            @Part("id_menu") RequestBody id_menu,
            @Part("date_added") RequestBody date_added,
            @Part MultipartBody.Part file);

    // Updating Foods details
    @FormUrlEncoded
    @POST("updateFoodItem/{id}")
    Call<Result> updateFoodItem(@Path("id") String id,
                                @Field("name") String name,
                                @Field("price") String price,
                                @Field("discount_price") String discount_price,
                                @Field("description") String description,
                                @Field("min_unit_amount") String min_unit_amount,
                                @Field("unit") String unit,
                                @Field("id_menu") String id_menu,
                                @Field("status") String status);

    // Updating Foods details with image
    @Multipart
    @POST("updateFoodItemImage")
    Call<Result> updateFoodItemImage(
            @Part("id") RequestBody id,
            @Part("name") RequestBody name,
            @Part("price") RequestBody price,
            @Part("discount_price") RequestBody discount_price,
            @Part("description") RequestBody description,
            @Part("min_unit_amount") RequestBody min_unit_amount,
            @Part("unit") RequestBody unit,
            @Part("id_menu") RequestBody id_menu,
            @Part("status") RequestBody status,
            @Part MultipartBody.Part file);

    //deleting Foods
    @DELETE("deleteFoodItem/{id}")
    Call<Result> deleteFoodItem(
            @Path("id") String id);

    //get all foods based on menu id
    @GET("getFoodByMenuIDAdmin/{id_menu}")
    Call<Result> getFoodByMenuIDAdmin(@Path("id_menu") String id_menu);

    //get order details
    @FormUrlEncoded
    @POST("getOrderDetails")
    Call<Result> getOrderDetails(@Field("id_order") String id_order);

    //Get order Details for Admin
    @FormUrlEncoded
    @POST("getOrderItemsAdmin")
    Call<Result> getOrderItemsAdmin(
            @Field("id_order") String id_order);

    //Get all order history for
    @GET("getAllOrderForAdmin")
    Call<Result> getAllOrderForAdmin();

    //Get order Details for Admin by delivery_date
    @FormUrlEncoded
    @POST("getAllOrderForAdminByDelDate")
    Call<Result> getAllOrderForAdminByDelDate(
            @Field("delivery_date") String delivery_date);

    //Update order Status --> ADMIN PART
    @FormUrlEncoded
    @POST("updateOrderStatus")
    Call<Result> updateOrderStatus(
            @Field("id_order") String id_order,
            @Field("id_shipper") String id_shipper,
            @Field("order_status") String order_status,
            @Field("payment_state") String payment_state,
            @Field("food_delivery_date") String food_delivery_date);

    //Get User Token
    @FormUrlEncoded
    @POST("getUserToken")
    Call<Result> getUserToken(
            @Field("id") String id,
            @Field("isServerToken") String isServerToken);

    // Uploading Shipper details
    @Multipart
    @POST("addShipper")
    Call<Result> addShipper(
            @Part("username") RequestBody username,
            @Part("password") RequestBody password,
            @Part("phone") RequestBody phone,
            @Part("address") RequestBody address,
            @Part MultipartBody.Part file);

    //get all Shippers
    @GET("getShippers")
    Call<Result> getShippers();

    // add Shipper Status
    @FormUrlEncoded
    @POST("updateShipperStatus")
    Call<Result> updateShipperStatus(
            @Field("id") String id,
            @Field("status") String status);

    // add new unit
    @FormUrlEncoded
    @POST("addUnit")
    Call<Result> addUnit(
            @Field("unit_type") String unit_type);

    // update unit
    @FormUrlEncoded
    @POST("updateUnit/{id}")
    Call<Result> updateUnit(
            @Path("id") String id,
            @Field("unit_type") String unit_type);

    //deleting unit
    @DELETE("deleteUnit/{id}")
    Call<Result> deleteUnit(
            @Path("id") String id);

    //get all unit
    @GET("getUnit")
    Call<Result> getUnit();

    // add new area
    @FormUrlEncoded
    @POST("addArea")
    Call<Result> addArea(
            @Field("city_name") String city_name,
            @Field("area_name") String area_name);

    // update area
    @FormUrlEncoded
    @POST("updateArea/{id}")
    Call<Result> updateArea(
            @Path("id") String id,
            @Field("city_name") String city_name,
            @Field("area_name") String area_name);


    //deleting Area
    @DELETE("deleteArea/{id}")
    Call<Result> deleteArea(
            @Path("id") String id);

    //get all Area
    @GET("getArea")
    Call<Result> getArea();

    // upload coupon
    @FormUrlEncoded
    @POST("addCoupon")
    Call<Result> addCoupon(
            @Field("name") String name,
            @Field("code") String code,
            @Field("type") String type,
            @Field("discount") String discount,
            @Field("discount_limit") String discount_limit,
            @Field("total") String total,
            @Field("start_date") String start_date,
            @Field("end_date") String end_date,
            @Field("uses_total") String uses_total,
            @Field("uses_customer") String uses_customer,
            @Field("date_added") String date_added);


    // update coupon
    @FormUrlEncoded
    @POST("updateCoupon")
    Call<Result> updateCoupon(
            @Field("id") String id,
            @Field("name") String name,
            @Field("code") String code,
            @Field("type") String type,
            @Field("discount") String discount,
            @Field("discount_limit") String discount_limit,
            @Field("total") String total,
            @Field("start_date") String start_date,
            @Field("end_date") String end_date,
            @Field("uses_total") String uses_total,
            @Field("uses_customer") String uses_customer);

    //deleting coupon
    @DELETE("deleteCoupon/{id}")
    Call<Result> deleteCoupon(
            @Path("id") String id);

    //get all coupons
    @GET("getCoupons")
    Call<Result> getCoupons();

    // upload Delivery Slots
    @FormUrlEncoded
    @POST("addDeliverySlot")
    Call<Result> addDeliverySlot(
            @Field("day") String day,
            @Field("start_time") String start_time,
            @Field("end_time") String end_time,
            @Field("allocation") String allocation);

    //get all time Slots
    @GET("getAllSlots")
    Call<Result> getAllSlots();


    // upload  Slots
    @FormUrlEncoded
    @POST("addSlot")
    Call<Result> addSlot(
            @Field("max_day") String max_day,
            @Field("start_time") String start_time,
            @Field("end_time") String end_time,
            @Field("order_time") String order_time,
            @Field("delivery_charge") String delivery_charge,
            @Field("delivery_free_amount") String delivery_free_amount);

    // update  Slots
    @FormUrlEncoded
    @POST("updateSlot")
    Call<Result> updateSlot(
            @Field("max_day") String max_day,
            @Field("start_time") String start_time,
            @Field("end_time") String end_time,
            @Field("order_time") String order_time,
            @Field("delivery_charge") String delivery_charge,
            @Field("delivery_free_amount") String delivery_free_amount);

    //get all time Slots
    @GET("getSlots")
    Call<Result> getSlots();

    //Get all order history for
    @FormUrlEncoded
    @POST("getAllOrder")
    Call<Result> getAllOrder(
            @Field("id_user") String id_user);

    // get order total amount based on date and order_status complete
    @FormUrlEncoded
    @POST("getTotalWalletAmount")
    Call<Result> getTotalWalletAmount(
            @Field("delivery_date") String delivery_date,
            @Field("order_status") String order_status);

    // get total amount received  based on transfer_date
    @FormUrlEncoded
    @POST("getRecTotalWalletAmount")
    Call<Result> getRecTotalWalletAmount(
            @Field("transfer_date") String transfer_date);

    // get total amount received  based on transfer_date
    @FormUrlEncoded
    @POST("getShippersForTransPending")
    Call<Result> getShippersForTransPending(
            @Field("date") String date);

    // get total amount received  based on transfer_date
    @FormUrlEncoded
    @POST("getShippersForTransCompleted")
    Call<Result> getShippersForTransCompleted(
            @Field("date") String date);

    // update wallet transfer table
    @FormUrlEncoded
    @POST("updateWalletTransfer")
    Call<Result> updateWalletTransfer(
            @Field("id") String id,
            @Field("id_order") String id_order,
            @Field("id_shipper") String id_shipper);

    //Get all Summary
    @FormUrlEncoded
    @POST("getSummary")
    Call<Result> getSummary(
            @Field("year") String year,
            @Field("month") String month);

    // get all ordered foods quantity
    @FormUrlEncoded
    @POST("getOrderedFoodQuantity")
    Call<Result> getOrderedFoodQuantity(
            @Field("delivery_date") String delivery_date);

    //Get all Completed order
    @FormUrlEncoded
    @POST("getAllCompletedOrderForShipper")
    Call<Result> getAllCompletedOrderForShipper(
            @Field("id_shipper") String id_shipper
    );

    //Get all Completed order based on date
    @FormUrlEncoded
    @POST("getAllCompletedOrderForShipperDate")
    Call<Result> getAllCompletedOrderForShipperDate(
            @Field("id_shipper") String id_shipper,
            @Field("delivery_date") String delivery_date
    );

    //Get all Completed order based on monthly date
    @FormUrlEncoded
    @POST("getAllCompletedOrderForShipperMonth")
    Call<Result> getAllCompletedOrderForShipperMonth(
            @Field("id_shipper") String id_shipper,
            @Field("year") String year,
            @Field("month") String month
    );


    // get total amount received
    @FormUrlEncoded
    @POST("getRecTotalWalletAmountShipper")
    Call<Result> getRecTotalWalletAmountShipper(
            @Field("id_shipper") String id_shipper);

    // get total amount received  based on transfer_date
    @FormUrlEncoded
    @POST("getRecTotalWalletAmountShipperDate")
    Call<Result> getRecTotalWalletAmountShipperDate(
            @Field("id_shipper") String id_shipper,
            @Field("transfer_date") String transfer_date);

    // get total amount received  based on monthly transfer_date
    @FormUrlEncoded
    @POST("getRecTotalWalletAmountShipperMonth")
    Call<Result> getRecTotalWalletAmountShipperMonth(
            @Field("id_shipper") String id_shipper,
            @Field("year") String year,
            @Field("month") String month);

    //get all reviews
    @GET("getAllReviews")
    Call<Result> getAllReviews();
}
