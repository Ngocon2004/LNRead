package me.etylix.lnread;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvSeries = view.findViewById(R.id.rv_new_series);
        rvSeries.setLayoutManager(new LinearLayoutManager(getContext()));

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
                        if (series.getSeriesGenre().contains(genre)) {
                            seriesList.add(series);
                        }
                    }
                    seriesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Series>> call, Throwable t) {
                // Handle error
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
