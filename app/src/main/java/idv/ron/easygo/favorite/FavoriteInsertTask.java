package idv.ron.easygo.favorite;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import idv.ron.easygo.order.Order;
import idv.ron.easygo.order.OrderProduct;

public class FavoriteInsertTask extends AsyncTask<Object, Integer, Order> {
    private final static String TAG = "FavoriteInsertTask";
    private final static String ACTION = "Favoriteinsert";

    @Override
    protected Order doInBackground(Object... params) {
        Gson gson = new Gson();
        String url = params[0].toString();
        String user_cellphone = params[1].toString();
        Integer favorite_food = Integer.valueOf(params[2].toString());
//        List<OrderProduct> cart = (List<OrderProduct>) params[2];
        String jsonIn;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", ACTION);
        jsonObject.addProperty("user_cellphone", user_cellphone);
        jsonObject.addProperty("favorite_food", favorite_food);
//        jsonObject.addProperty("cart", gson.toJson(cart));
        try {
            jsonIn = getRemoteData(url, jsonObject.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }
        return gson.fromJson(jsonIn, Order.class);
    }

    private String getRemoteData(String url, String jsonOut) throws IOException {
        StringBuilder text = new StringBuilder();
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true); // allow inputs
        connection.setDoOutput(true); // allow outputs
        connection.setUseCaches(false); // do not use a cached copy
        connection.setRequestMethod("POST");
        connection.setRequestProperty("charset", "UTF-8");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        bw.write(jsonOut);
        Log.d(TAG, "jsonOut: " + jsonOut);
        bw.close();

        int responseCode = connection.getResponseCode();

        if (responseCode == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
            }
        } else {
            Log.d(TAG, "response code: " + responseCode);
        }
        connection.disconnect();
        Log.d(TAG, "jsonIn: " + text);
        return text.toString();
    }
}