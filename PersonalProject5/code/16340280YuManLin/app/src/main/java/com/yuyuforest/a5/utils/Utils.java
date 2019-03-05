package com.yuyuforest.a5.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class Utils {

    public static InputStream requestURL(String urlstr) throws IOException {
        URL url = new URL(urlstr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        return connection.getInputStream();
    }

    public static Bitmap readBitmapFromInputStream(InputStream is) throws IOException{
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        return bitmap;
    }

    public static ArrayList<Bitmap> readFramesFromInputStream(InputStream is, int count, int img_x_len, int img_y_len,
                                                     int img_x_size, int img_y_size) throws IOException{
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        ArrayList<Bitmap> frames = new ArrayList<>();
        if(bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) return null;
        int x = 0;
        int y = 0;
        for(int i = 0; i < count; i++) {
            frames.add(Bitmap.createBitmap(bitmap, x, y, img_x_size, img_y_size));
            x += img_x_size;
            if(i % img_x_len == img_x_len - 1) {
                x = 0;
                y += img_y_size;
            }
        }
        return frames;
    }

    public static String readStringFromInputStream(InputStream is) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String currentLine;
        String total = "";
        while((currentLine = reader.readLine()) != null) {
            if(currentLine.length() > 0) {
                total += currentLine;
            }
        }
        return total;
    }

    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
