package me.etylix.lnread;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment implements SeriesAdapter.OnSeriesClickListener {

    private RecyclerView rvFavorites;
    private SeriesAdapter seriesAdapter;
    private List<Series> seriesList = new ArrayList<>();
    private List<Series> filteredList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        rvFavorites = view.findViewById(R.id.rv_favorites);
        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        seriesAdapter = new SeriesAdapter(getContext(), filteredList, this);
        rvFavorites.setAdapter(seriesAdapter);

        fetchSeries(); // Gọi API để lấy tất cả series
        loadFavorites(); // Tải danh sách yêu thích từ database

        return view;
    }

    private void loadFavorites() {
        new Thread(() -> {
            List<SeriesEntity> favorites = DatabaseSingleton.getInstance(getContext()).getDatabase().seriesDao().getAllSeries();
            getActivity().runOnUiThread(() -> {
                filteredList.clear();
                for (SeriesEntity seriesEntity : favorites) {
                    Series series = new Series();
                    series.setSeriesName(seriesEntity.getSeriesName());
                    series.setSeriesImg(seriesEntity.getSeriesImg());
                    series.setSeriesAuthor(seriesEntity.getSeriesAuthor());
                    series.setSeriesPlot(seriesEntity.getSeriesPlot());
                    filteredList.add(series);
                }
                seriesAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    private void fetchSeries() {
        ApiService apiService = RetrofitClient.getInstance().getRetrofit().create(ApiService.class);
        Call<List<Series>> call = apiService.getSeries();
        call.enqueue(new Callback<List<Series>>() {
            @Override
            public void onResponse(Call<List<Series>> call, Response<List<Series>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    seriesList.clear();
                    seriesList.addAll(response.body());
                } else {
                    Toast.makeText(getContext(), "Không thể tải dữ liệu series", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Series>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSeriesClick(Series series) {
        // Tìm series khớp chính xác từ seriesList (danh sách từ API)
        Series matchedSeries = findSeriesFromApi(series.getSeriesName());
        if (matchedSeries != null) {
            SeriesDetailFragment detailFragment = new SeriesDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("series", matchedSeries);
            detailFragment.setArguments(bundle);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            Toast.makeText(getContext(), "Không tìm thấy series: " + series.getSeriesName(), Toast.LENGTH_SHORT).show();
        }
    }

    private Series findSeriesFromApi(String seriesName) {
        for (Series series : seriesList) {
            if (series.getSeriesName().equalsIgnoreCase(seriesName)) { // Khớp chính xác (bỏ qua case)
                return series;
            }
        }
        return null;
    }
}