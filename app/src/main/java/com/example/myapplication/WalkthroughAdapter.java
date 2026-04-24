package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout; // Changed import
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class WalkthroughAdapter extends RecyclerView.Adapter<WalkthroughAdapter.WalkthroughViewHolder> {

    private List<WalkthroughActivity.WalkthroughItem> walkthroughItems;

    public WalkthroughAdapter(List<WalkthroughActivity.WalkthroughItem> walkthroughItems) {
        this.walkthroughItems = walkthroughItems;
    }

    @NonNull
    @Override
    public WalkthroughViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WalkthroughViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_walkthrough, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull WalkthroughViewHolder holder, int position) {
        holder.setData(walkthroughItems.get(position));
    }

    @Override
    public int getItemCount() {
        return walkthroughItems.size();
    }

    class WalkthroughViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout layoutItem; // Changed from LinearLayout to match new XML
        private TextView tvTitle, tvDesc;

        public WalkthroughViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layoutItem);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
        }

        void setData(WalkthroughActivity.WalkthroughItem item) {
            // This sets the walkthrough render (the phone image) as the full background
            layoutItem.setBackgroundResource(item.image);
            tvTitle.setText(item.title);
            tvDesc.setText(item.desc);
        }
    }
}