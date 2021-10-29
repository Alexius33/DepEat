package com.alexiusdev.depeat.services;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.alexiusdev.depeat.ui.HttpRequestType;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class RestController {

    private static final String TAG = RestController.class.getSimpleName();
    private final String BASE_URL;
    private Properties properties;
    private final RequestQueue queue;
    private final Context context;

    public RestController(Context context) {
        this.context = context;
        properties = getApplicationProperties("app.properties");


        BASE_URL = properties.getProperty("url.rest.basePath");
        queue = Volley.newRequestQueue(context);
    }

    public void getRestaurants(Response.Listener<String> success, Response.ErrorListener error) {
        String[] paramNames = {"authCode"};
        String endpoint = resolveUrl(properties.getProperty("url.rest.getRestaurants"), paramNames, properties.getProperty("application.authCode"));
        requestHelper(HttpRequestType.GET, endpoint, success, error);
    }

    public void getRestaurantProducts(String restaurantId, Response.Listener<String> success, Response.ErrorListener error) {
        String[] paramNames = {"authCode", "restaurantId"};
        String endpoint = resolveUrl(properties.getProperty("url.rest.getProducts"), paramNames, properties.getProperty("application.authCode"), restaurantId);
        requestHelper(HttpRequestType.GET, endpoint, success, error);
    }


    /****************** PRIVATE METHODS ******************/
    private Properties getApplicationProperties(String file) {
        try {
            properties = new Properties();
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(file);
            properties.load(inputStream);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return properties;
    }

    private String resolveUrl(String url, final String[] paramNames, final String... paramValues) {
        url = StringUtils.replace(url, "{", "");
        url = StringUtils.replace(url, "}", "");
        return StringUtils.replaceEach(url, paramNames, paramValues);
    }

    private void requestHelper(HttpRequestType httpRequestType, String endpoint, Response.Listener<String> success, Response.ErrorListener error) {
        if (httpRequestType == null || StringUtils.isEmpty(endpoint) || success == null || error == null)
            throw new IllegalArgumentException("Parameters cannot be null");
        requestHelper(httpRequestType, null, endpoint, success, error);
    }

    private void requestHelper(HttpRequestType httpRequestType, JSONArray body, String endpoint, Response.Listener<String> success, Response.ErrorListener error) {
        switch (httpRequestType) {
            case GET:
                getRequest(endpoint, success, error);
                break;
            case POST:
                if (body == null || body.length() == 0)
                    throw new IllegalArgumentException("Body in POST req cannot be null nor empty");
                postRequest(endpoint, body, success, error);
                break;
        }
    }

    private void getRequest(String endPoint, Response.Listener<String> success, Response.ErrorListener error) {
        Log.d(TAG, "API link: " + BASE_URL + endPoint);

        StringRequest request = new StringRequest(Request.Method.GET, BASE_URL.concat(endPoint), success, error);
        queue.add(request);
    }

    private void postRequest(String endPoint, final JSONArray body, Response.Listener<String> success, Response.ErrorListener error) {
        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL.concat(endPoint), success, error) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                if (body != null) {
                    Log.d("body", body.toString());
                    return body.toString().getBytes(StandardCharsets.UTF_8);
                }
                return super.getBody();
            }
        };
        queue.add(request);
    }

}
