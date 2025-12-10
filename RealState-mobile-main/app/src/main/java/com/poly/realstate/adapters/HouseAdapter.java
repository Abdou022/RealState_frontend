package com.poly.realstate.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.poly.realstate.HouseDetailsActivity;
import com.poly.realstate.R;
import com.poly.realstate.models.House;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HouseAdapter extends RecyclerView.Adapter<HouseAdapter.HouseViewHolder> {

    private Context context;
    private List<House> houses;

    // URL de base pour les images
    private static final String IMAGE_BASE_URL = "http://192.168.1.11:8000/uploads/images/";

    public HouseAdapter(Context context, List<House> houses) {
        this.context = context;
        this.houses = houses;
    }

    @NonNull
    @Override
    public HouseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_house, parent, false);
        return new HouseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HouseViewHolder holder, int position) {
        House house = houses.get(position);
        holder.title.setText(house.getTitle());
        holder.price.setText("Prix: "+house.getPrice());
        holder.address.setText("Adresse: "+house.getAddress());
        holder.surface.setText("Surface: "+house.getSurface()+ "m");
        holder.rooms.setText("Chambres: "+house.getRooms());

        // Charger l'image avec Picasso depuis le serveur
        if (house.getImage() != null && !house.getImage().isEmpty()) {
            Picasso.get()
                    .load(IMAGE_BASE_URL + house.getImage()) // nom de l'image sur le serveur
                    .placeholder(R.drawable.ic_home)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.ic_home);
        }

        // Click listener pour ouvrir HouseDetailsActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, HouseDetailsActivity.class);
            intent.putExtra("id", house.getId());
            intent.putExtra("title", house.getTitle());
            intent.putExtra("price", house.getPrice());
            intent.putExtra("address", house.getAddress());
            intent.putExtra("surface", house.getSurface());
            intent.putExtra("rooms", house.getRooms());
            intent.putExtra("image", house.getImage());
            intent.putExtra("ownerName", house.getOwnerName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return houses.size();
    }

    static class HouseViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, price, address, surface, rooms;

        public HouseViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.houseImage);
            title = itemView.findViewById(R.id.houseTitle);
            price = itemView.findViewById(R.id.housePrice);
            address = itemView.findViewById(R.id.houseAddress);
            surface = itemView.findViewById(R.id.houseSurface);
            rooms = itemView.findViewById(R.id.houseRooms);
        }
    }
}
