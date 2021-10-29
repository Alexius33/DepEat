package com.alexiusdev.depeat.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexiusdev.depeat.R;
import com.alexiusdev.depeat.datamodels.Product;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater inflater;
    private final Context context;
    private List<Product> products;
    private OnQuantityChangedListener onQuantityChangedListener;

    public ProductAdapter(Context context, List<Product> products) {
        inflater = LayoutInflater.from(context);
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_shop, viewGroup, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int index) {
        ProductViewHolder productViewHolder = (ProductViewHolder) viewHolder;

        productViewHolder.productName.setText(products.get(index).getName());
        productViewHolder.productTotalPrice.setText(context.getString(R.string.currency).concat(String.format(Locale.getDefault(), "%.2f", products.get(index).getPrice() * products.get(index).getQuantity())));
        productViewHolder.productSinglePrice.setText(context.getString(R.string.currency).concat(String.format(Locale.getDefault(), "%.2f", products.get(index).getPrice())));
        productViewHolder.productQty.setText(String.valueOf(products.get(index).getQuantity()));
        Glide.with(context).load(products.get(index).getImageUrl()).into(productViewHolder.foodIv);

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public interface OnQuantityChangedListener {
        void onChange(double price);
    }

    public OnQuantityChangedListener getOnQuantityChangedListener() {
        return onQuantityChangedListener;
    }

    public void setOnQuantityChangedListener(OnQuantityChangedListener onQuantityChangedListener) {
        this.onQuantityChangedListener = onQuantityChangedListener;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView productName, productTotalPrice, productSinglePrice, productQty;
        private final ImageView foodIv;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.food_tv);
            productTotalPrice = itemView.findViewById(R.id.total_price_item);
            productSinglePrice = itemView.findViewById(R.id.single_price);
            productQty = itemView.findViewById(R.id.quantity_tv);
            ImageView addBtn = itemView.findViewById(R.id.plus_iv);
            ImageView removeBtn = itemView.findViewById(R.id.minus_iv);
            foodIv = itemView.findViewById(R.id.food_iv);

            addBtn.setOnClickListener(this);
            removeBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Product product = products.get(getAdapterPosition());

            if (view.getId() == R.id.plus_iv) {
                product.increaseQuantity();
                onQuantityChangedListener.onChange(product.getPrice());
                notifyItemChanged(getAdapterPosition());
            } else if (view.getId() == R.id.minus_iv) {
                if (product.getQuantity() == 0) return;
                product.decreaseQuantity();
                onQuantityChangedListener.onChange(product.getPrice() * -1);
                notifyItemChanged(getAdapterPosition());
            }
        }

    }
}