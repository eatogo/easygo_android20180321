package idv.ron.easygo.main;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import idv.ron.easygo.R;

import idv.ron.easygo.cart.CartActivity;
import idv.ron.easygo.favorite.FavoriteActivity;
import idv.ron.easygo.membership.MemberActivity;
;
import idv.ron.easygo.order.OrderGetAllActivity;
import idv.ron.easygo.order.OrderProduct;
import idv.ron.easygo.product.ProductGetAllActivity;

public class Common {
    // 模擬器連Tomcat
//    public static String URL = "http://10.1.33.202:8080/EasyGo_MySQL_Web/";
    public static String URL = "http://10.0.2.2:8080/EasyGo_MySQL_Web/";
//    public static String URL = "http://54.65.214.19:8080/EasyGo_MySQL_Web/";

    // 偏好設定檔案名稱
    public final static String PREF_FILE = "preference";
    private static final String TAG = "Common";

    // 要讓商品在購物車內順序能夠一定，且使用RecyclerView顯示時需要一定順序，List較佳
    public static List<OrderProduct> FAVORITE = new ArrayList<>();
    public static List<OrderProduct> CART = new ArrayList<>();

    // 功能分類
    public final static Category[] CATEGORIES = {
            new Category(0, "User", R.drawable.user, MemberActivity.class),
            new Category(1, "Products", R.drawable.product, ProductGetAllActivity.class),
            new Category(2, "favorite", R.drawable.ic_favorite_black_24dp, FavoriteActivity.class),
            new Category(3, "Shopping cart", R.drawable.cart_empty, CartActivity.class),
            new Category(4, "order", R.drawable.setting, OrderGetAllActivity.class)};



    // check if the device connect to the network
    public static boolean networkConnected(Activity activity) {
        ConnectivityManager conManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager != null ? conManager.getActiveNetworkInfo() : null;
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void showToast(Context context, int messageResId) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public static byte[] bitmapToPNG(Bitmap srcBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 轉成PNG不會失真，所以quality參數值會被忽略
        srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    // 設定長寬不超過scaleSize
    public static Bitmap downSize(Bitmap srcBitmap, int newSize) {
        // 如果欲縮小的尺寸過小，就直接定為128
        if (newSize <= 50) {
            newSize = 128;
        }
        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();
        String text = "source image size = " + srcWidth + "x" + srcHeight;
        Log.d(TAG, text);
        int longer = Math.max(srcWidth, srcHeight);

        if (longer > newSize) {
            double scale = longer / (double) newSize;
            int dstWidth = (int) (srcWidth / scale);
            int dstHeight = (int) (srcHeight / scale);
            srcBitmap = Bitmap.createScaledBitmap(srcBitmap, dstWidth, dstHeight, false);
            System.gc();
            text = "\nscale = " + scale + "\nscaled image size = " +
                    srcBitmap.getWidth() + "x" + srcBitmap.getHeight();
            Log.d(TAG, text);
        }
        return srcBitmap;
    }
}
