package com.alexiusdev.depeat.ui.activities;

import static com.alexiusdev.depeat.ui.Utility.LOGIN_REQUEST_CODE;
import static com.alexiusdev.depeat.ui.Utility.PRICE;
import static com.alexiusdev.depeat.ui.Utility.RESTAURANT_ADDRESS;
import static com.alexiusdev.depeat.ui.Utility.RESTAURANT_ID;
import static com.alexiusdev.depeat.ui.Utility.RESTAURANT_IMAGE_URL;
import static com.alexiusdev.depeat.ui.Utility.RESTAURANT_MIN_ORDER;
import static com.alexiusdev.depeat.ui.Utility.RESTAURANT_NAME;
import static com.alexiusdev.depeat.ui.Utility.RESTAURANT_PRODUCTS;
import static com.alexiusdev.depeat.ui.Utility.showToast;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexiusdev.depeat.R;
import com.alexiusdev.depeat.datamodels.Product;
import com.alexiusdev.depeat.datamodels.Restaurant;
import com.alexiusdev.depeat.services.RestController;
import com.alexiusdev.depeat.ui.adapters.ProductAdapter;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Locale;

public class ShopActivity extends AppCompatActivity implements View.OnClickListener, ProductAdapter.OnQuantityChangedListener, Response.Listener<String>, Response.ErrorListener {

    private static final String TAG = ShopActivity.class.getSimpleName();
    private Button checkoutBtn;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private RecyclerView productRv;
    private LinearLayoutManager layoutManager;
    private ProductAdapter adapter;
    private TextView restaurantNameTv, restaurantAddressTv, totalPriceTv, minOrderTv;
    private ProgressBar progressBar;
    private Restaurant restaurant;
    private ImageView restaurantIv, mapsIv;
    private RelativeLayout nothingRl;
    private static double total;
    private RestController restController;
    private ArrayList<Product> products;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        restaurant = getRestaurantFromIntent();
        setTitle(restaurant.getName());
        total = 0;
        setContentView(R.layout.activity_shop);

        mAuth = FirebaseAuth.getInstance();
        checkoutBtn = findViewById(R.id.checkout_btn);
        progressBar = findViewById(R.id.progressbar);
        restaurantNameTv = findViewById(R.id.restaurant_name_tv);
        restaurantAddressTv = findViewById(R.id.restaurant_address_tv);
        totalPriceTv = findViewById(R.id.total_price_value);
        minOrderTv = findViewById(R.id.min_order_value);
        mapsIv = findViewById(R.id.maps_iv);
        restaurantIv = findViewById(R.id.restaurant_iv);
        productRv = findViewById(R.id.product_rv);
        nothingRl = findViewById(R.id.nothing_rl);

        checkoutBtn.setOnClickListener(this);
        mapsIv.setOnClickListener(this);

        restaurantNameTv.setText(restaurant.getName());
        restaurantAddressTv.setText(restaurant.getAddress());
        progressBar.setMax((int) restaurant.getMinOrder() * 100);

        restController = new RestController(this);
        restController.getRestaurantProducts(restaurant.getId(), this, this);

        layoutManager = new LinearLayoutManager(this);
        adapter = new ProductAdapter(this, products);
        adapter.setOnQuantityChangedListener(this);

        productRv.setAdapter(adapter);
        productRv.setLayoutManager(layoutManager);

        minOrderTv.setText(getString(R.string.currency).concat(String.format(Locale.getDefault(), "%.2f", restaurant.getMinOrder())));

        //initialise stuff at 0
        progressBar.setProgress(0);
        totalPriceTv.setText(getString(R.string.currency).concat(String.format(Locale.getDefault(), "%.2f", 0.0)));

        Glide.with(this).load(restaurant.getImageUrl()).into(restaurantIv);


        if (CollectionUtils.isEmpty(products)) {
            nothingRl.setVisibility(View.VISIBLE);
            productRv.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.checkout_btn):
                if (currentUser == null) {
                    showToast(this, getString(R.string.login_required));
                    startActivityForResult(new Intent(this, LoginActivity.class), LOGIN_REQUEST_CODE);
                } else
                    startCheckoutActivity();
                break;
            case (R.id.maps_iv):
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=".concat(Uri.encode(restaurantAddressTv.getText().toString())))));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE && resultCode == RESULT_OK) {
            startCheckoutActivity();
        }
    }

    private Restaurant getRestaurantFromIntent() {
        Restaurant restaurant = new Restaurant();
        if (getIntent().getExtras() != null) {
            String id = getIntent().getExtras().getString(RESTAURANT_ID);
            String name = getIntent().getExtras().getString(RESTAURANT_NAME);
            String address = getIntent().getExtras().getString(RESTAURANT_ADDRESS);
            String imageUrl = getIntent().getExtras().getString(RESTAURANT_IMAGE_URL);
            double minOrder = getIntent().getExtras().getDouble(RESTAURANT_MIN_ORDER);

            restaurant = new Restaurant(id, name, address, imageUrl, minOrder);
        }
        return restaurant;
    }

    @Override
    public void onChange(double price) {
        Log.i("PREZZO", String.valueOf(price));
        total += price;
        updateUi(total);
        Log.i("PREZZO_TOTAL", String.valueOf(total));
    }

    private void updateUi(double total) {
        totalPriceTv.setText(getString(R.string.currency).concat(String.format(Locale.getDefault(), "%.2f", total)));
        enableCheckout(total);
        updateProgress((int) total * 100);
    }

    private void updateProgress(int progress) {
        progressBar.setProgress(progress);
    }

    public void enableCheckout(double total) {
        checkoutBtn.setEnabled(total >= restaurant.getMinOrder());
        checkoutBtn.setTextColor(total >= restaurant.getMinOrder() ? getResources().getColor(R.color.primary_text) : getResources().getColor(R.color.secondary_text));
    }

    private void startCheckoutActivity() {
        startActivity(new Intent(this, CheckoutActivity.class)
                .putExtra(RESTAURANT_NAME, restaurant.getName())
                .putExtra(RESTAURANT_PRODUCTS, products)
                .putExtra(PRICE, total)
                .putExtra(RESTAURANT_ID, restaurant.getId()));
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        nothingRl.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResponse(String response) {
        Log.d(TAG, response);
        products = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                products.add(new Product(jsonArray.getJSONObject(i)));
            }
            adapter.setProducts(products);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            nothingRl.setVisibility(View.VISIBLE);
            productRv.setVisibility(View.GONE);
        }

        if (CollectionUtils.isNotEmpty(products)) {
            nothingRl.setVisibility(View.GONE);
            productRv.setVisibility(View.VISIBLE);
        }
    }
}
