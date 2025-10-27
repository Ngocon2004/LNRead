package me.etylix.lnread;

import android.content.Context;
import androidx.room.Room;

public class DatabaseSingleton {
    // Singleton instance
    private static volatile DatabaseSingleton instance = null;
    private AppDatabase database;

    // Private constructor để ngăn khởi tạo trực tiếp
    private DatabaseSingleton(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "series-database")
                    .build();
        }
    }

    // Thread-safe getInstance method (Double-Checked Locking pattern)
    public static DatabaseSingleton getInstance(Context context) {
        if (instance == null) {
            synchronized (DatabaseSingleton.class) {
                if (instance == null) {
                    instance = new DatabaseSingleton(context);
                }
            }
        }
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }
}

