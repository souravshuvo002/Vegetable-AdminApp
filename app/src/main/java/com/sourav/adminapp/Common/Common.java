package com.sourav.adminapp.Common;

import com.sourav.adminapp.Api.FCMClient;
import com.sourav.adminapp.Api.IFCMService;
import com.sourav.adminapp.Model.TextScroll;
import com.sourav.adminapp.Model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Common {

    public static String User_email = "";
    public static String User_phone = "";
    public static String User_ID = "";
    public static User currentUser;

    public static String menu_id = "";
    public static String menu_name = "";
    public static String id_order = "";
    public static String fragment_state = "";
    public static TextScroll text_scroll ;

    public static String delivery_charge = "";


    public static String DATE = "";


    private static final String FCM_API = "https://fcm.googleapis.com/";

    public static IFCMService getFCMService(){
        return FCMClient.getClient(FCM_API).create(IFCMService.class);
    }

    public static String convertCodeToStatus(String code)
    {
        if(code.equals("1"))
            return "Pending";
        else if(code.equals("2"))
            return "Accepted";
        else if(code.equals("3"))
            return "Rejected";
        else if(code.equals("4"))
            return "Shipping";
        else if(code.equals("5"))
            return "Completed";
        else if(code.equals("6"))
            return "Cancelled";
        else
            return "No result";
    }

    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}