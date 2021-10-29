package com.alexiusdev.depeat.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexiusdev.depeat.R;
import com.alexiusdev.depeat.datamodels.Product;

import java.util.ArrayList;
import java.util.Locale;

public class CheckoutAdapter extends RecyclerView.Adapter {
    private final LayoutInflater inflater;
    private final Context context;
    private final ArrayList<Product> products;

    public CheckoutAdapter(Context context, ArrayList<Product> products) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.products = products;
        Log.d("adapter constructor", products.toString());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_checkout, viewGroup, false);
        return new CheckoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int index) {
        Product product = products.get(index);
        CheckoutViewHolder checkoutViewHolder = (CheckoutViewHolder) viewHolder;

        checkoutViewHolder.quantityTv.setText(String.valueOf(product.getQuantity()).concat("x"));
        checkoutViewHolder.nameTv.setText(product.getName());
        checkoutViewHolder.priceTv.setText(context.getString(R.string.currency).concat(String.format(Locale.getDefault(), "%.2f", product.getPrice())));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }


    private static class CheckoutViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView quantityTv, nameTv, priceTv;

        public CheckoutViewHolder(@NonNull View itemView) {
            super(itemView);
            quantityTv = itemView.findViewById(R.id.item_checkout_qty);
            nameTv = itemView.findViewById(R.id.item_checkout_name);
            priceTv = itemView.findViewById(R.id.item_checkout_price);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
