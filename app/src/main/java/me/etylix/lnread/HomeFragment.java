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

public class HomeFragment extends Fragment implements SeriesAdapter.OnSeriesClickListener {

    private RecyclerView rvNewSeries, rvPopularSeries;
    private SeriesAdapter newSeriesAdapter, popularSeriesAdapter;
    private List<Series> seriesList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvNewSeries = view.findViewById(R.id.rv_new_series);
        rvPopularSeries = view.findViewById(R.id.rv_popular_series);

        rvNewSeries.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPopularSeries.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));

        newSeriesAdapter = new SeriesAdapter(getContext(), seriesList, this);
        popularSeriesAdapter = new SeriesAdapter(getContext(), seriesList, this);

        rvNewSeries.setAdapter(newSeriesAdapter);
        rvPopularSeries.setAdapter(popularSeriesAdapter);

        fetchSeries();

        return view;
    }

    private void fetchSeries() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Series>> call = apiService.getSeries();
        call.enqueue(new Callback<List<Series>>() {
            @Override
            public void onResponse(Call<List<Series>> call, Response<List<Series>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    seriesList.clear();
                    seriesList.addAll(response.body());
                    newSeriesAdapter.notifyDataSetChanged();
                    popularSeriesAdapter.notifyDataSetChanged();
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