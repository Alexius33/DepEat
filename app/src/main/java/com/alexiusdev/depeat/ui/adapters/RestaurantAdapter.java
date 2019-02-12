package com.alexiusdev.depeat.ui.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alexiusdev.depeat.R;
import com.alexiusdev.depeat.datamodels.Restaurant;
import com.alexiusdev.depeat.ui.activities.ShopActivity;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Locale;

public class RestaurantAdapter extends RecyclerView.Adapter {
    private LayoutInflater inflater;
    private ArrayList<Restaurant> restaurants;
    private boolean isGridMode;
    private Context context;

    public RestaurantAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.restaurants = new ArrayList<>();
        this.context = context;
    }

    public RestaurantAdapter(Context context, ArrayList<Restaurant> restaurants){
        inflater = LayoutInflater.from(context);
        this.restaurants = restaurants;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        int layout = isGridMode ? R.layout.item_main_grid : R.layout.item_main_cards;
        View view = inflater.inflate(layout, viewGroup, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int index) {
        RestaurantViewHolder restaurantViewHolder = (RestaurantViewHolder) viewHolder;
        restaurantViewHolder.restaurantNameTV.setText(restaurants.get(index).getName());
        restaurantViewHolder.restaurantAddressTV.setText(restaurants.get(index).getAddress());
        restaurantViewHolder.restaurantMinCheckoutValueTV.append(String.valueOf(restaurants.get(index).getMinOrder()));
        restaurantViewHolder.restaurantRatingTV.setText(String.format(Locale.getDefault(),"%.1f", restaurants.get(index).getRatingFloat()));
        restaurantViewHolder.ratingBar.setProgress(restaurants.get(index).getRatingInt());
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public void setRestaurants(ArrayList<Restaurant> restaurants){
        this.restaurants = restaurants;
        notifyDataSetChanged();
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView restaurantNameTV, restaurantAddressTV, restaurantMinCheckoutValueTV, restaurantRatingTV;
        public RatingBar ratingBar;
        public MaterialCardView restaurantCard;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantNameTV = itemView.findViewById(R.id.name_tv);
            restaurantAddressTV = itemView.findViewById(R.id.restaurant_address_tv);
            restaurantMinCheckoutValueTV = itemView.findViewById(R.id.min_checkout_value);
            restaurantRatingTV = itemView.findViewById(R.id.rating_number_tv);
            ratingBar = itemView.findViewById(R.id.rating_stars);

            restaurantCard = itemView.findViewById(R.id.restaurant_card);
            restaurantCard.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == restaurantCard.getId()){
                context.startActivity(new Intent(context, ShopActivity.class));
            }
        }
    }

    public boolean isGridMode() {
        return isGridMode;
    }

    public void setGridMode(boolean gridMode) {
        isGridMode = gridMode;
    }

}