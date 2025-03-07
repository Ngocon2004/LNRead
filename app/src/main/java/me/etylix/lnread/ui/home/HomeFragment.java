package me.etylix.lnread.ui.home;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import me.etylix.lnread.R;
import me.etylix.lnread.Story;
import me.etylix.lnread.StoryAdapter;
import me.etylix.lnread.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private static String JSON_URL = "https://konovn.net/json/konovn.json";
    RecyclerView recyclerView;
    StoryAdapter storyAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<Story> storyList;
    ArrayList<String> storyName = new ArrayList<>();
    ArrayList<String> storyAuthor = new ArrayList<>();
    ArrayList<String> storyImage = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        storyList = new ArrayList<>();
        recyclerView = root.findViewById(R.id.rvStoryByDate);
        GetData getData = new GetData();
        new GetData().execute();
        return root;
    }

    public class GetData extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {

            String current ="";
            try {
                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL(JSON_URL);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                    int data = inputStreamReader.read();
                    while (data != -1){
                        current += (char) data;
                        data = inputStreamReader.read();
                    }

                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
            return current;

        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("story");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    Story story = new Story();
                    story.setName(jsonObject1.getString("name"));
                    story.setAuthor(jsonObject1.getString("author"));
                    story.setImg(jsonObject1.getString("img"));

                    storyList.add(story);

                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            PutData2Rv(storyList);
        }
    }

    private void PutData2Rv(List<Story> storyList){
        StoryAdapter adapter = new StoryAdapter(this.getActivity(), storyList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}