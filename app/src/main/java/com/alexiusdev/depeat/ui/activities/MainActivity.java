package com.alexiusdev.depeat.ui.activities;

import static com.alexiusdev.depeat.ui.Utility.EMAIL_KEY;
import static com.alexiusdev.depeat.ui.Utility.showToast;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.alexiusdev.depeat.R;
import com.alexiusdev.depeat.datamodels.Restaurant;
import com.alexiusdev.depeat.services.RestController;
import com.alexiusdev.depeat.ui.adapters.RestaurantAdapter;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Response.Listener<String>, Response.ErrorListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private MenuItem gridViewMenuItem, cardViewMenuItem, profileMenuItem, logoutMenuItem, loginMenuItem;
    private FirebaseAuth mAuth;
    private RecyclerView.LayoutManager layoutManager;
    private RestaurantAdapter adapter;
    private RestController restController;
    private RelativeLayout loadingPanel, nothingRl;

    FirebaseUser currentUser;
    RecyclerView restaurantRV;
    private List<Restaurant> restaurants;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        restaurantRV = findViewById(R.id.restaurant_rv);
        loadingPanel = findViewById(R.id.loadingPanel);
        nothingRl = findViewById(R.id.nothing_rl);
        restaurantRV.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RestaurantAdapter(this);
        restaurantRV.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();

        if (getIntent().getExtras() != null && getIntent().getExtras().getString(EMAIL_KEY) != null)
            showToast(this, getString(R.string.welcome) + " " + getIntent().getExtras().getString(EMAIL_KEY));

        restController = new RestController(this);
        restController.getRestaurants(this, this);

        if (CollectionUtils.isEmpty(restaurants)) {
            loadingPanel.setVisibility(View.GONE);
            restaurantRV.setVisibility(View.GONE);
            nothingRl.setVisibility(View.VISIBLE);
        }

       /*try {
            for(int i = 0; i < restController.readFromDB().length(); i++) {
                restaurants.add(new Restaurant(restController.readFromDB().getJSONObject(i)));
            }
            adapter.setRestaurants(restaurants);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

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
        profileMenuItem = menu.findItem(R.id.profile_menu);
        if (currentUser == null) {
            loginMenuItem.setVisible(true);
            logoutMenuItem.setVisible(false);
            profileMenuItem.setVisible(false);
        } else {
            loginMenuItem.setVisible(false);
            logoutMenuItem.setVisible(true);
            profileMenuItem.setVisible(true);
        }

        if (adapter.isGridMode()) {
            cardViewMenuItem.setVisible(true);
            gridViewMenuItem.setVisible(false);
        } else {
            cardViewMenuItem.setVisible(false);
            gridViewMenuItem.setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();

        if (itemId == R.id.login_menu) {
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        } else if (itemId == R.id.logout_menu) {
            mAuth.signOut();
            showToast(this, getString(R.string.user_logged_out));
            startActivity(new Intent(this, MainActivity.class));
            return true;
        } else if (itemId == R.id.grid_menu) {
            cardViewMenuItem.setVisible(true);
            gridViewMenuItem.setVisible(false);
            setLayoutManager();
            return true;
        } else if (itemId == R.id.card_menu) {
            cardViewMenuItem.setVisible(false);
            gridViewMenuItem.setVisible(true);
            setLayoutManager();
            return true;
        } else if (itemId == R.id.profile_menu) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
    }

    private void setLayoutManager() {
        layoutManager = adapter.isGridMode() ? new LinearLayoutManager(this) : new StaggeredGridLayoutManager(2, 1);
        adapter.setGridMode(!adapter.isGridMode());
        restaurantRV.setLayoutManager(layoutManager);
        restaurantRV.setAdapter(adapter);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        showToast(this, error.getMessage());
        Log.d("error", error.getMessage() != null ? error.getMessage() : "");
        nothingRl.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResponse(String response) {
        Log.d(TAG, response);
        restaurants = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                restaurants.add(new Restaurant(jsonArray.getJSONObject(i)));
            }
            adapter.setRestaurants(restaurants);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            restaurantRV.setVisibility(View.GONE);
            loadingPanel.setVisibility(View.GONE);
            nothingRl.setVisibility(View.VISIBLE);
        }

        if (CollectionUtils.isNotEmpty(restaurants)) {
            nothingRl.setVisibility(View.GONE);
            loadingPanel.setVisibility(View.GONE);
            restaurantRV.setVisibility(View.VISIBLE);
        }
    }


}
