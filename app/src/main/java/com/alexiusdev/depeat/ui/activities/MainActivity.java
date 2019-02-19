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
import com.alexiusdev.depeat.services.RestController;
import com.alexiusdev.depeat.ui.adapters.RestaurantAdapter;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static com.alexiusdev.depeat.ui.Utility.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Response.Listener<String>, Response.ErrorListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private MenuItem logoutMenuItem;
    private MenuItem loginMenuItem;
    private MenuItem gridViewMenuItem;
    private MenuItem cardViewMenuItem;
    private FirebaseAuth mAuth;
    private RecyclerView.LayoutManager layoutManager;
    private RestaurantAdapter adapter;
    private RestController restController;
    private static final int LOGIN_REQUEST_CODE = 2001;
    private Menu menu;


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

        if(getIntent().getExtras() != null && getIntent().getExtras().getString(EMAIL_KEY) != null)
            showToast(this, getString(R.string.welcome) + " " + getIntent().getExtras().getString(EMAIL_KEY));

        restController = new RestController(this);
        restController.getRequest(Restaurant.END_POINT,this,this);
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
        this.menu = menu;
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG,"requestCode" + requestCode);
        Log.d(TAG, "resultCode" + resultCode);
        if(requestCode == LOGIN_REQUEST_CODE && resultCode == RESULT_OK){
            menu.findItem(R.id.login_menu).setTitle(R.string.profile).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case (R.id.login_menu):
                startActivityForResult(new Intent(this, LoginActivity.class), LOGIN_REQUEST_CODE);
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

    @Override
    public void onErrorResponse(VolleyError error) {
        //error.networkResponse.statusCode
        showToast(this,error.getMessage());
    }

    @Override
    public void onResponse(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            for(int i = 0; i < jsonArray.length(); i++)
                restaurants.add(new Restaurant(jsonArray.getJSONObject(i)));
            adapter.setRestaurants(restaurants);
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
        }
    }
}
