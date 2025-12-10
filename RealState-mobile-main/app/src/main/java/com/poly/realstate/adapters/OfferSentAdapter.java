package com.poly.realstate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.poly.realstate.R;
import com.poly.realstate.models.OfferSent;

import java.util.List;

public class OfferSentAdapter extends RecyclerView.Adapter<OfferSentAdapter.OfferSentViewHolder> {

    private Context context;
    private List<OfferSent> offers;

    public OfferSentAdapter(Context context, List<OfferSent> offers) {
        this.context = context;
        this.offers = offers;
    }

    @NonNull
    @Override
    public OfferSentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_offer_sent, parent, false);
        return new OfferSentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferSentViewHolder holder, int position) {
        OfferSent offer = offers.get(position);
        holder.txtHouseTitle.setText(offer.getHouseTitle());
        holder.txtStatus.setText("Status: " + offer.getStatus());
        holder.txtCreatedAt.setText("Sent at: " + offer.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    static class OfferSentViewHolder extends RecyclerView.ViewHolder {
        TextView txtHouseTitle, txtStatus, txtCreatedAt;

        public OfferSentViewHolder(@NonNull View itemView) {
            super(itemView);
            txtHouseTitle = itemView.findViewById(R.id.txtHouseTitle);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
        }
    }
}
