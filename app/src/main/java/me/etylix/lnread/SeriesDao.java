package me.etylix.lnread;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface SeriesDao {
    @Insert
    void insert(SeriesEntity series);

    @Query("SELECT * FROM favorite_series")
    List<SeriesEntity> getAllFavorites();

    @Query("DELETE FROM favorite_series WHERE seriesName = :seriesName")
    void deleteBySeriesName(String seriesName);

    @Query("SELECT * FROM favorite_series WHERE seriesName = :seriesName")
    SeriesEntity getSeriesByName(String seriesName);

    @Query("SELECT * FROM favorite_series")
    List<SeriesEntity> getAllSeries();
}
