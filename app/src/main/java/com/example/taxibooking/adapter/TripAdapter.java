package com.example.taxibooking.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taxibooking.data.model.Trip;
import com.example.taxibooking.databinding.TripItemBinding;
import com.example.taxibooking.utils.OnItemClickListener;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {
    private Context context;
    private OnItemClickListener listener;
    private List<Trip> list;


    public TripAdapter(Context context, List<Trip> list, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.list = list;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TripItemBinding binding = TripItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TripViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Trip model = list.get(holder.getAdapterPosition());
        holder.binding.tvDest.setText(model.getDestination());
        holder.binding.tvPickup.setText(model.getPickUp());
        holder.binding.tvFare.setText("€" + model.getAmount());

        holder.binding.btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        private TripItemBinding binding;

        public TripViewHolder(@NonNull TripItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
