package me.etylix.lnread;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import me.etylix.lnread.ui.home.HomeFragment;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {
    Context context;
    List<Story> storyData;
    RecyclerView rvStory;

    public StoryAdapter(Context context, List<Story> storyData){
        this.context = context;
        this.storyData = storyData;
    }


    @NonNull
    @Override
    public StoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StoryAdapter.ViewHolder holder, int position) {
        Glide.with(context).load(storyData.get(position).getImg()).into((holder.storyImg));
        holder.storyName.setText(storyData.get(position).getName());
        holder.storyAuthor.setText(storyData.get(position).getAuthor());
    }

    @Override
    public int getItemCount() {
        return storyData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView storyImg;
        TextView storyName;
        TextView storyAuthor;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            storyImg = itemView.findViewById(R.id.story_img);
            storyName = itemView.findViewById(R.id.story_name);
            storyAuthor = itemView.findViewById(R.id.story_author);
        }
    }
}
