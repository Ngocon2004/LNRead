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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SeriesViewHolder> {
    private List<Series> seriesList;
    private Context context;
    private OnSeriesClickListener listener;

    public interface OnSeriesClickListener {
        void onSeriesClick(Series series);
    }

    public SeriesAdapter(Context context, List<Series> seriesList, OnSeriesClickListener listener) {
        this.context = context;
        this.seriesList = seriesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SeriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_series, parent, false);
        return new SeriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeriesViewHolder holder, int position) {
        Series series = seriesList.get(position);
        holder.tvSeriesName.setText(series.getSeriesName());
        holder.tvSeriesAuthor.setText(series.getSeriesAuthor());
        Glide.with(context).load(series.getSeriesImg()).apply(new RequestOptions().transform(new RoundedCorners(37))).into(holder.ivSeriesImage);
        holder.itemView.setOnClickListener(v -> listener.onSeriesClick(series));
    }

    @Override
    public int getItemCount() {
        if(seriesList==null) return 0;
        return seriesList.size();
    }

    static class SeriesViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSeriesImage;
        TextView tvSeriesName, tvSeriesAuthor;

        public SeriesViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSeriesImage = itemView.findViewById(R.id.iv_series_image);
            tvSeriesName = itemView.findViewById(R.id.tv_series_name);
            tvSeriesAuthor = itemView.findViewById(R.id.tv_series_author);
        }
    }
}
