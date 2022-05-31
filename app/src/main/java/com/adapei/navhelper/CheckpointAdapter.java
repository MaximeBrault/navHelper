package com.adapei.navhelper;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CheckpointAdapter extends RecyclerView.Adapter<CheckpointAdapter.ViewHolder> implements Filterable {

    private List<Checkpoint> checkpoints;
    private List<Checkpoint> filteredCheckpoints;
    private Context context;
    private OnItemClickListener mItemClickListener;

    public interface OnItemClickListener { void onItemClickListener(View view, Checkpoint checkpoint); }

    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageCheckpoint;
        TextView textCheckpoint;

        ViewHolder(View itemView) {
            super(itemView);
            imageCheckpoint = itemView.findViewById(R.id.imageCheckpoint);
            textCheckpoint = itemView.findViewById(R.id.textCheckpoint);
        }
    }

    public CheckpointAdapter(Context context, List<Checkpoint> checkpoints) {
        this.checkpoints = checkpoints;
        this.filteredCheckpoints = checkpoints;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.checkpoint_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Checkpoint checkpoint = this.filteredCheckpoints.get(holder.getAdapterPosition());
        Glide.with(context.getApplicationContext()).load(Uri.parse(checkpoint.getPictureUri().toString())).into(holder.imageCheckpoint);
        holder.textCheckpoint.setText(checkpoint.getDisplayName());
        if(checkpoint.getDisplayName().length() > 10) holder.textCheckpoint.setText(checkpoint.getDisplayName().substring(0,9)+"...");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.onItemClickListener(holder.itemView, checkpoint);
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return filteredCheckpoints.size();
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString().toLowerCase().trim();

                if (charString.isEmpty()) {
                    filteredCheckpoints = checkpoints;
                } else {
                    List<Checkpoint> filteredList = new ArrayList<>();

                    for (Checkpoint checkpoint : checkpoints) {
                        if (checkpoint.getDisplayName().toLowerCase().contains(charString)) {
                            filteredList.add(checkpoint);
                        }
                    }
                    filteredCheckpoints = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredCheckpoints;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                notifyDataSetChanged();
            }
        };
    }
}