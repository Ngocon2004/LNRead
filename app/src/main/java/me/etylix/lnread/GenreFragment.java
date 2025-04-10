package me.etylix.lnread;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreFragment extends Fragment implements GenreAdapter.OnGenreClickListener {

    private RecyclerView rvGenres;
    private GenreAdapter genreAdapter;
    private List<String> genreList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genre, container, false);

//        rvGenres = view.findViewById(R.id.rv_genres);
//        rvGenres.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        genreAdapter = new GenreAdapter(getContext(), genreList, this);
//        rvGenres.setAdapter(genreAdapter);
//
//        fetchGenres();

        return view;
    }

    private void fetchGenres() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Series>> call = apiService.getSeries();
        call.enqueue(new Callback<List<Series>>() {
            @Override
            public void onResponse(Call<List<Series>> call, Response<List<Series>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Set<String> genres = new HashSet<>();
                    for (Series series : response.body()) {
                        genres.addAll(series.getSeriesGenre());
                    }
                    genreList.clear();
                    genreList.addAll(genres);
                    genreAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Series>> call, Throwable t) {
                // Handle error
            }
        });
    }

    @Override
    public void onGenreClick(String genre) {
        GenreSeriesFragment genreSeriesFragment = new GenreSeriesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("genre", genre);
        genreSeriesFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, genreSeriesFragment)
                .addToBackStack(null)
                .commit();
    }
}