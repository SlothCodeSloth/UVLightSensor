package com.example.bletest3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IntroViewPagerAdapter extends RecyclerView.Adapter<IntroViewPagerAdapter.ViewHolder>  {
    Context mContext;
    List<ScreenItem> mListScreen;

    public IntroViewPagerAdapter(Context mContext, List<ScreenItem> mListScreen) {
        this.mContext = mContext;
        this.mListScreen = mListScreen;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_screen, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScreenItem item = mListScreen.get(position);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.imgSlide.setImageResource(item.getScreenImg());
    }

    @Override
    public int getItemCount() {
        return mListScreen.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSlide;
        TextView title, description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSlide = itemView.findViewById(R.id.intro_img);
            title = itemView.findViewById(R.id.intro_title);
            description = itemView.findViewById(R.id.intro_description);
        }
    }
}