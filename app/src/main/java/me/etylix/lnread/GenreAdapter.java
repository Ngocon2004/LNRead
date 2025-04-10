package me.etylix.lnread;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {
    private List<String> genreList;
    private Context context;
    private OnGenreClickListener listener;

    public interface OnGenreClickListener {
        void onGenreClick(String genre);
    }

    public GenreAdapter(Context context, List<String> genreList, OnGenreClickListener listener) {
        this.context = context;
        this.genreList = genreList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_genre, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        String genre = genreList.get(position);
        holder.tvGenreName.setText(genre);
        holder.itemView.setOnClickListener(v -> listener.onGenreClick(genre));
    }

    @Override
    public int getItemCount() {
        if(genreList == null) return 0;
        return genreList.size();
    }

    static class GenreViewHolder extends RecyclerView.ViewHolder {
        TextView tvGenreName;

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGenreName = itemView.findViewById(R.id.tv_genre_name);
        }
    }
}
