package com.alexiusdev.depeat.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.alexiusdev.depeat.R;
import com.alexiusdev.depeat.datamodels.Restaurant;
import com.alexiusdev.depeat.ui.adapters.RestaurantAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static com.alexiusdev.depeat.ui.Utility.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MenuItem logoutMenuItem;
    private MenuItem loginMenuItem;
    private MenuItem gridViewMenuItem;
    private MenuItem cardViewMenuItem;
    private FirebaseAuth mAuth;
    private RecyclerView.LayoutManager layoutManager;
    private RestaurantAdapter adapter;

    FirebaseUser currentUser;
    RecyclerView restaurantRV;
    ArrayList<Restaurant> restaurants = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        restaurantRV = findViewById(R.id.restaurant_rv);
        restaurantRV.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RestaurantAdapter(this);
        restaurantRV.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();

        if(getIntent().getExtras() != null)
            showToast(this, getString(R.string.welcome) + " " + getIntent().getExtras().getString(EMAIL_KEY));

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://5ba19290ee710f0014dd764c.mockapi.io/api/v1/restaurant";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG,response);
                        //Start parsing
                        try {
                            JSONArray restaurantJsonArray = new JSONArray(response);
                            for(int i = 0; i < restaurantJsonArray.length(); i++){
                                Restaurant restaurant = new Restaurant(restaurantJsonArray.getJSONObject(i));
                                restaurants.add(restaurant);
                            }
                            adapter.setRestaurants(restaurants);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,error.getMessage());
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        loginMenuItem = menu.findItem(R.id.login_menu);
        logoutMenuItem = menu.findItem(R.id.logout_menu);
        gridViewMenuItem = menu.findItem(R.id.grid_menu);
        cardViewMenuItem = menu.findItem(R.id.card_menu);
        if(currentUser == null) {
            loginMenuItem.setVisible(true);
            logoutMenuItem.setVisible(false);
        } else {
            loginMenuItem.setVisible(false);
            logoutMenuItem.setVisible(true);
        }if(adapter.isGridMode()){
            cardViewMenuItem.setVisible(true);
            gridViewMenuItem.setVisible(false);
        }else{
            cardViewMenuItem.setVisible(false);
            gridViewMenuItem.setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case (R.id.login_menu):
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case (R.id.cart_menu):
                startActivity(new Intent(this, ShopActivity.class));
                return true;
            case (R.id.logout_menu):
                mAuth.signOut();
                showToast(this,getString(R.string.user_logged_out));
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case (R.id.grid_menu):
                cardViewMenuItem.setVisible(true);
                gridViewMenuItem.setVisible(false);
                setLayoutManager();
                return true;
            case (R.id.card_menu):
                cardViewMenuItem.setVisible(false);
                gridViewMenuItem.setVisible(true);
                setLayoutManager();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
    }

    private ArrayList<Restaurant> getRestaurants(){
        return restaurants;
    }

    private void setLayoutManager(){
        layoutManager = adapter.isGridMode() ? new LinearLayoutManager(this) : new StaggeredGridLayoutManager(2,1);
        adapter.setGridMode(!adapter.isGridMode());
        restaurantRV.setLayoutManager(layoutManager);
        restaurantRV.setAdapter(adapter);
    }
}
