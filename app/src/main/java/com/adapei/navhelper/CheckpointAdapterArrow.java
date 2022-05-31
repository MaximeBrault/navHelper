package com.adapei.navhelper;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CheckpointAdapterArrow extends RecyclerView.Adapter<CheckpointAdapterArrow.ViewHolder> {

    private List<Checkpoint> checkpoints;
    private Context context;
    private OnItemClickListener mItemClickListener;

    public interface OnItemClickListener { void onItemClickListener(View view, Checkpoint checkpoint); }

    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageCheckpoint;

        ViewHolder(View itemView) {
            super(itemView);
            imageCheckpoint = itemView.findViewById(R.id.imageCheckpoint);
        }
    }

    public CheckpointAdapterArrow(Context context, List<Checkpoint> checkpoints) {
        this.checkpoints = checkpoints;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.checkpoint_arrow_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Checkpoint checkpoint = this.checkpoints.get(position);
        Glide.with(context.getApplicationContext()).load(Uri.parse(checkpoint.getPictureUri().toString())).into(holder.imageCheckpoint);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.onItemClickListener(holder.itemView, checkpoint);
            }
        });

        DrawableCompat.setTint(DrawableCompat.wrap(holder.itemView.findViewById(R.id.borderCheckpoint).getBackground()), context.getResources().getColor(
                        checkpoint.isMarked ?
                                R.color.greenADAPEI :
                                R.color.colorPrimary));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return checkpoints.size();
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}