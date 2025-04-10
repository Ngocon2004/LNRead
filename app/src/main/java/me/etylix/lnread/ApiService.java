package me.etylix.lnread;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("json/konovn.json")
    Call<List<Series>> getSeries();
}
