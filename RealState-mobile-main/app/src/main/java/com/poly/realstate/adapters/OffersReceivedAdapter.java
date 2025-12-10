package com.poly.realstate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.poly.realstate.ApiService;
import com.poly.realstate.R;
import com.poly.realstate.models.OfferReceivedItem;
import com.poly.realstate.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OffersReceivedAdapter extends RecyclerView.Adapter<OffersReceivedAdapter.ViewHolder> {

    private final List<OfferReceivedItem> offers;
    private final ApiService apiService;

    public OffersReceivedAdapter(List<OfferReceivedItem> offers) {
        this.offers = offers;
        this.apiService = RetrofitClient.getInstance().getApi();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offer_received, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OfferReceivedItem offer = offers.get(position);

        holder.tvHouseTitle.setText("Maison: " + offer.getHouseTitle());
        holder.tvApplicantName.setText("Demandeur: " + offer.getApplicantName());
        holder.tvStatus.setText("Statut: " + offer.getStatus());

        holder.btnApprove.setOnClickListener(v -> acceptOffer(holder, offer, position));
        holder.btnReject.setOnClickListener(v -> rejectOffer(holder, offer, position));
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    private void acceptOffer(ViewHolder holder, OfferReceivedItem offer, int position) {
        Call<Void> call = apiService.acceptOffer(offer.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(holder.itemView.getContext(), "Offre acceptée", Toast.LENGTH_SHORT).show();
                    offer.setStatus("approved");
                    notifyItemChanged(position);
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Erreur serveur", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(holder.itemView.getContext(), "Erreur réseau: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void rejectOffer(ViewHolder holder, OfferReceivedItem offer, int position) {
        Call<Void> call = apiService.rejectOffer(offer.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(holder.itemView.getContext(), "Offre rejetée", Toast.LENGTH_SHORT).show();
                    offer.setStatus("rejected");
                    notifyItemChanged(position);
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Erreur serveur", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(holder.itemView.getContext(), "Erreur réseau: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHouseTitle, tvApplicantName, tvStatus;
        Button btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHouseTitle = itemView.findViewById(R.id.tvHouseTitle);
            tvApplicantName = itemView.findViewById(R.id.tvApplicantName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
