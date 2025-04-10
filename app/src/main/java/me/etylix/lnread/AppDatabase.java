package me.etylix.lnread;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {SeriesEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SeriesDao seriesDao();
}
