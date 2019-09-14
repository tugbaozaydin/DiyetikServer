package com.project.diyetikserver.Common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.project.diyetikserver.Model.Request;
import com.project.diyetikserver.Model.User;
import com.project.diyetikserver.Remote.APIService;
import com.project.diyetikserver.Remote.IGeoCoordinates;
import com.project.diyetikserver.Remote.RetrofitClient;
import com.project.diyetikserver.Service.FCMRetrofitClient;

import retrofit2.Retrofit;

public class Common {
    public static User currentUser;
    public static Request currentRequest;
    public static final String UPDATE = "Update";

    public static String PHONE_TEXT= "userPhone";

    public static final String DELETE = "Delete";

    public static final String baseUrl = "https://maps.googleapis.com";
    private  static  final String BASE_URL = "https://fcm.googleapis.com/";
    public static final String convertCodeToStatus(String code) {
        if (code.equals("0"))
            return "Placed";
        else if (code.equals("1"))
            return "On my way";
        else
            return "Shipped";
    }

    public static APIService getFCMService() {
        return FCMRetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static IGeoCoordinates getGeoCodeService() {
        return RetrofitClient.getClient(baseUrl).create(IGeoCoordinates.class);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0, pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }
}