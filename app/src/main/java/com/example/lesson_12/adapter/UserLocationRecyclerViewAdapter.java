package com.example.lesson_12.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lesson_12.R;
import com.example.lesson_12.database.entity.UserLocation;

import java.util.ArrayList;
import java.util.List;

public class UserLocationRecyclerViewAdapter extends RecyclerView.Adapter<UserLocationRecyclerViewAdapter.ViewHolder> {
    private final Context context;
    private List<UserLocation> userLocations = new ArrayList<>();

    public UserLocationRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.location_list_item, parent, false);
        return new UserLocationRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewLatitude.setText(String.valueOf(userLocations.get(position).getLatitude()));
        holder.textViewLongitude.setText(String.valueOf(userLocations.get(position).getLongitude()));
    }

    public void setUserLocations(List<UserLocation> userLocations) {
        this.userLocations = userLocations;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return userLocations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewLongitude;
        private final TextView textViewLatitude;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewLatitude = itemView.findViewById(R.id.textView_listItem_latitude);
            textViewLongitude = itemView.findViewById(R.id.textView_listItem_longitude);

        }
    }

}
