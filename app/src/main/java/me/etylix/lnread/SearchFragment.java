package me.etylix.lnread;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment implements SeriesAdapter.OnSeriesClickListener {

    private EditText etSearch;
    private RecyclerView rvSearchResults;
    private SeriesAdapter seriesAdapter;
    private List<Series> seriesList = new ArrayList<>();
    private List<Series> filteredList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        etSearch = view.findViewById(R.id.et_search);
        rvSearchResults = view.findViewById(R.id.rv_search_results);

        rvSearchResults.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        seriesAdapter = new SeriesAdapter(getContext(), filteredList, this);
        rvSearchResults.setAdapter(seriesAdapter);

        fetchSeries();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSeries(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

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
                    filterSeries("");
                }
            }

            @Override
            public void onFailure(Call<List<Series>> call, Throwable t) {
                // Handle error
            }
        });
    }

    private void filterSeries(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
//            filteredList.addAll(seriesList);
            //Show all series
        } else {
            for (Series series : seriesList) {
                if (series.getSeriesName().toLowerCase().contains(query.toLowerCase()) ||
                        series.getSeriesAuthor().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(series);
                }
            }
        }
        seriesAdapter.notifyDataSetChanged();
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