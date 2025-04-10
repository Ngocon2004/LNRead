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

public class FavoritesFragment extends Fragment implements SeriesAdapter.OnSeriesClickListener {

    private RecyclerView rvFavorites;
    private SeriesAdapter seriesAdapter;
    private List<Series> favoriteSeriesList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        rvFavorites = view.findViewById(R.id.rv_favorites);
        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));

        seriesAdapter = new SeriesAdapter(getContext(), favoriteSeriesList, this);
        rvFavorites.setAdapter(seriesAdapter);

        loadFavorites();

        return view;
    }

    private void loadFavorites() {
        new Thread(() -> {
            List<SeriesEntity> entities = ((MainActivity) getActivity()).getDatabase().seriesDao().getAllFavorites();
            favoriteSeriesList.clear();
            for (SeriesEntity entity : entities) {
                Series series = new Series();
                series.setSeriesName(entity.getSeriesName());
                series.setSeriesImg(entity.getSeriesImg());
                series.setSeriesAuthor(entity.getSeriesAuthor());
                series.setSeriesPlot(entity.getSeriesPlot());
                favoriteSeriesList.add(series);
            }
            getActivity().runOnUiThread(() -> seriesAdapter.notifyDataSetChanged());
        }).start();
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