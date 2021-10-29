package com.alexiusdev.depeat.ui.activities;

import static com.alexiusdev.depeat.ui.Utility.PRICE;
import static com.alexiusdev.depeat.ui.Utility.RESTAURANT_ID;
import static com.alexiusdev.depeat.ui.Utility.RESTAURANT_NAME;
import static com.alexiusdev.depeat.ui.Utility.RESTAURANT_PRODUCTS;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexiusdev.depeat.R;
import com.alexiusdev.depeat.datamodels.Product;
import com.alexiusdev.depeat.services.RestController;
import com.alexiusdev.depeat.ui.adapters.CheckoutAdapter;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CheckoutActivity extends AppCompatActivity implements Response.Listener<String>, Response.ErrorListener {
    private TextView nameTv, priceTv;
    private Button payBtn;
    private CheckoutAdapter adapter;
    private List<Product> products;
    private RecyclerView checkoutRv;
    private String restaurantId;


    @SuppressWarnings({"unchecked"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        nameTv = findViewById(R.id.name_tv);
        payBtn = findViewById(R.id.pay_btn);
        checkoutRv = findViewById(R.id.product_rv);
        priceTv = findViewById(R.id.price_tv);

        payBtn.setOnClickListener(v -> paymentRequest(String.valueOf(getIntent().getExtras().get(RESTAURANT_ID)), "123123", priceTv.getText().toString()));

        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().get(RESTAURANT_PRODUCTS) instanceof ArrayList) { //get products from intent
            nameTv.setText(getIntent().getExtras().getString(RESTAURANT_NAME));
            products = (List<Product>) getIntent().getExtras().get(RESTAURANT_PRODUCTS);
            priceTv.append(" ".concat(getString(R.string.currency)).concat(String.format(Locale.getDefault(), "%.2f", getIntent().getExtras().getDouble(PRICE))));

            products.removeIf(product -> product.getQuantity() > 0); //remove products with 0 qty from the list
        }

        adapter = new CheckoutAdapter(this, products);
        checkoutRv.setAdapter(adapter);
        checkoutRv.setLayoutManager(new LinearLayoutManager(this));

    }

    private void paymentRequest(String restaurantId, String userId, String total) {
        RestController restController = new RestController(this);
        JSONArray body = new JSONArray();
        body.put(restaurantId).put(userId).put(total).put(jsonArrayFromJsonProductFromArrayList(products));
        //fixme create payment method in the controller
        //restController.postRequest("orders",body, this,this);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d("order error response", error.toString());
    }

    @Override
    public void onResponse(String response) {
        Log.d("order response", response);
    }

    private JSONArray jsonArrayFromJsonProductFromArrayList(List<Product> products) {
        return products.stream()
                .map(Product::toJSONObject)
                .collect(Collectors.collectingAndThen(Collectors.toList(), JSONArray::new));
    }
}
