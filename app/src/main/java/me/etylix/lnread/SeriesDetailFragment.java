package me.etylix.lnread;

import static me.etylix.lnread.WebViewHelper.openInWebView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

public class SeriesDetailFragment extends Fragment {

    private ImageView ivSeriesImage;
    private TextView tvSeriesName, tvSeriesAuthor, tvSeriesPlot;
    private Button btnRead, btnFavorite;
    private RecyclerView rvChapters;
    private ChapterAdapter chapterAdapter;
    private Series series;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            series = (Series) getArguments().getSerializable("series", Series.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_series_detail, container, false);

        ivSeriesImage = view.findViewById(R.id.iv_series_image);
        tvSeriesName = view.findViewById(R.id.tv_series_name);
        tvSeriesAuthor = view.findViewById(R.id.tv_series_author);
        tvSeriesPlot = view.findViewById(R.id.tv_series_plot);
        btnRead = view.findViewById(R.id.btn_read);
        btnFavorite = view.findViewById(R.id.btn_favorite);
        rvChapters = view.findViewById(R.id.rv_chapters);

        // Set data
        Glide.with(this).load(series.getSeriesImg()).apply(new RequestOptions().transform(new RoundedCorners(37))).into(ivSeriesImage);
        tvSeriesName.setText(series.getSeriesName());
        tvSeriesAuthor.setText(series.getSeriesAuthor());
        tvSeriesPlot.setText(series.getSeriesPlot());
        chkFavorite();

        // Set up chapters
        rvChapters.setLayoutManager(new LinearLayoutManager(getContext()));
        chapterAdapter = new ChapterAdapter(getContext(), series.getSeriesChapter());
        rvChapters.setAdapter(chapterAdapter);
        chkFavorite();

        // Read button (opens the first chapter in a Custom Tab)
        btnRead.setOnClickListener(v -> {
            if (series.getSeriesChapter() != null && !series.getSeriesChapter().isEmpty()) {
                String chapterUrl = series.getSeriesChapter().get(0).getURL();
                WebViewHelper.openInWebView(getActivity(), getParentFragmentManager(), chapterUrl, R.id.fragment_container);
            } else {
                Toast.makeText(getContext(), "Không có chương nào để đọc", Toast.LENGTH_SHORT).show();
            }
        });

        // Favorite button

        btnFavorite.setOnClickListener(v -> {
            new Thread(() -> {
                SeriesEntity existingSeries = ((MainActivity) getActivity()).getDatabase().seriesDao().getSeriesByName(series.getSeriesName());
                if (existingSeries == null) {
                    SeriesEntity seriesEntity = new SeriesEntity();
                    seriesEntity.setSeriesName(series.getSeriesName());
                    seriesEntity.setSeriesImg(series.getSeriesImg());
                    seriesEntity.setSeriesAuthor(series.getSeriesAuthor());
                    seriesEntity.setSeriesPlot(series.getSeriesPlot());
                    ((MainActivity) getActivity()).getDatabase().seriesDao().insert(seriesEntity);
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Đã lưu vào yêu thích", Toast.LENGTH_SHORT).show());
                } else {
                    ((MainActivity) getActivity()).getDatabase().seriesDao().deleteBySeriesName(series.getSeriesName());
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show());
                }
                chkFavorite();
            }).start();
        });

        return view;
    }

    private void chkFavorite() {
        new Thread(() -> {
            SeriesEntity existingSeries = ((MainActivity) getActivity()).getDatabase().seriesDao().getSeriesByName(series.getSeriesName());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (existingSeries != null) {
                        btnFavorite.setText("Bỏ yêu thích");
                    }
                    else {
                        btnFavorite.setText("Yêu thích");
                    }
                }
            });

        }).start();
    }
}