package me.etylix.lnread;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreSeriesFragment extends Fragment implements SeriesAdapter.OnSeriesClickListener {

    private RecyclerView rvSeries;
    private TextView tvGenreTitle; // To display the genre name
    private SeriesAdapter seriesAdapter;
    private List<Series> seriesList = new ArrayList<>();
    private String genre;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            genre = getArguments().getString("genre");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genre_series, container, false);

        tvGenreTitle = view.findViewById(R.id.tv_genre_title);
        rvSeries = view.findViewById(R.id.rv_series);

        // Set the genre title
        tvGenreTitle.setText("Thể loại: " + genre);

        rvSeries.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        seriesAdapter = new SeriesAdapter(getContext(), seriesList, this);
        rvSeries.setAdapter(seriesAdapter);

        fetchSeriesByGenre();

        return view;
    }

    private void fetchSeriesByGenre() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Series>> call = apiService.getSeries();
        call.enqueue(new Callback<List<Series>>() {
            @Override
            public void onResponse(Call<List<Series>> call, Response<List<Series>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    seriesList.clear();
                    for (Series series : response.body()) {
                        if (series.getSeriesGenre() != null && series.getSeriesGenre().contains(genre)) {
                            seriesList.add(series);
                        }
                    }
                    seriesAdapter.notifyDataSetChanged();
                    if (seriesList.isEmpty()) {
                        Toast.makeText(getContext(), "Không có truyện nào thuộc thể loại " + genre, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Không thể tải danh sách truyện", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Series>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSeriesClick(Series series) {
        SeriesDetailFragment detailFragment = new SeriesDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("series", series);
        detailFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }
}