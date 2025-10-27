# 📘 Áp Dụng Singleton Pattern Vào Dự Án LNRead

## 📑 Mục Lục
1. [Singleton Pattern là gì?](#1-singleton-pattern-là-gì)
2. [Tại sao áp dụng Singleton Pattern?](#2-tại-sao-áp-dụng-singleton-pattern)
3. [Các thành phần cần áp dụng](#3-các-thành-phần-cần-áp-dụng)
4. [Implementation chi tiết](#4-implementation-chi-tiết)
5. [So sánh Trước và Sau](#5-so-sánh-trước-và-sau)
6. [Lợi ích đạt được](#6-lợi-ích-đạt-được)
7. [Thread Safety](#7-thread-safety)
8. [Best Practices](#8-best-practices)

---

## 1. Singleton Pattern là gì?

**Singleton Pattern** là một Design Pattern đảm bảo một class chỉ có **một và chỉ một instance** duy nhất trong toàn bộ application. Đồng thời cung cấp một **điểm truy cập global** đến instance đó.

### Đặc điểm chính:
- ✅ Chỉ cho phép tạo 1 instance
- ✅ Cung cấp điểm truy cập toàn cục
- ✅ Trì hoãn khởi tạo (Lazy initialization)
- ✅ Thread-safe

---

## 2. Tại sao áp dụng Singleton Pattern?

### Vấn đề trong dự án LNRead:

#### ❌ **Vấn đề 1: Database (AppDatabase)**
```java
// TRƯỚC: Database được tạo ở MainActivity
public class MainActivity extends AppCompatActivity {
    private AppDatabase database;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        database = Room.databaseBuilder(...).build(); // Tạo database
    }
    
    public AppDatabase getDatabase() {
        return database;
    }
}

// Fragments phải truy cập qua MainActivity
public class FavoritesFragment extends Fragment {
    private void loadFavorites() {
        // Phải cast về MainActivity
        ((MainActivity) getActivity()).getDatabase().seriesDao().getAllSeries();
    }
}
```

**Nhược điểm:**
- Dependency quá chặt giữa Fragment và Activity
- Khó test và maintain
- Nếu Activity bị destroy, database instance có thể mất
- Không đảm bảo chỉ có 1 database instance trong toàn app

#### ❌ **Vấn đề 2: RetrofitClient**
```java
// TRƯỚC: Static method nhưng không thread-safe
public class RetrofitClient {
    private static Retrofit retrofit = null;
    
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()...build(); // NOT THREAD-SAFE!
        }
        return retrofit;
    }
}
```

**Nhược điểm:**
- Trong môi trường multi-thread, có thể tạo nhiều instance
- Race condition có thể xảy ra
- Không đảm bảo memory safety

---

## 3. Các thành phần cần áp dụng

Dự án LNRead cần áp dụng Singleton Pattern cho:

### 🎯 **1. RetrofitClient** (API Networking)
- Lý do: Retrofit instance nặng, không nên tạo nhiều lần
- Lợi ích: Tiết kiệm memory, đảm bảo consistent connection pool

### 🎯 **2. AppDatabase** (Room Database)  
- Lý do: Room Database tốn nhiều tài nguyên, cần reuse
- Lợi ích: Tránh memory leak, performance tốt hơn

---

## 4. Implementation chi tiết

### 🔧 **Implementation 1: RetrofitClient Singleton**

#### **Code Implementation:**

```java
package me.etylix.lnread;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // 1. Singleton instance với volatile để đảm bảo visibility
    private static volatile RetrofitClient instance = null;
    
    // 2. Retrofit object
    private Retrofit retrofit;
    
    // 3. OkHttpClient configuration (static final để reuse)
    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .callTimeout(2, TimeUnit.MINUTES)
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .build();
    
    // 4. PRIVATE CONSTRUCTOR - Ngăn khởi tạo từ bên ngoài
    private RetrofitClient() {
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://konovn.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    
    // 5. THREAD-SAFE getInstance với Double-Checked Locking pattern
    public static RetrofitClient getInstance() {
        if (instance == null) {                              // First check (không lock)
            synchronized (RetrofitClient.class) {           // Lock class
                if (instance == null) {                      // Second check (trong lock)
                    instance = new RetrofitClient();         // Tạo instance
                }
            }
        }
        return instance;
    }
    
    // 6. Public method để lấy Retrofit
    public Retrofit getRetrofit() {
        return retrofit;
    }
}
```

#### **Giải thích từng phần:**

##### **a. `volatile` keyword:**
```java
private static volatile RetrofitClient instance = null;
```
- **Mục đích**: Đảm bảo visibility giữa các threads
- **Vì sao cần**: Khi một thread thay đổi `instance`, các thread khác thấy ngay thay đổi
- **Không có `volatile`**: Thread có thể cache giá trị cũ → có thể tạo nhiều instance

##### **b. Private Constructor:**
```java
private RetrofitClient() { ... }
```
- **Mục đích**: Ngăn khởi tạo từ bên ngoài class
- **Kết quả**: Chỉ có thể tạo instance qua `getInstance()`
- **Thử khởi tạo**: `new RetrofitClient()` → **COMPILE ERROR**

##### **c. Double-Checked Locking Pattern:**
```java
public static RetrofitClient getInstance() {
    if (instance == null) {                    // ✓ Check 1: Nhanh, không lock
        synchronized (RetrofitClient.class) {  // ✓ Lock class
            if (instance == null) {              // ✓ Check 2: Trong lock
                instance = new RetrofitClient();
            }
        }
    }
    return instance;
}
```

**Tại sao cần 2 lần check?**

```java
// ❌ KHÔNG ĐÚNG - Chỉ check 1 lần:
public static RetrofitClient getInstance() {
    synchronized (RetrofitClient.class) {     // Lock mỗi lần gọi → SLOW!
        if (instance == null) {
            instance = new RetrofitClient();
        }
    }
    return instance;
}
```
**Vấn đề**: Mỗi lần gọi `getInstance()` đều phải lock → Performance kém

```java
// ❌ KHÔNG ĐÚNG - Không thread-safe:
public static RetrofitClient getInstance() {
    if (instance == null) {              // Thread A: instance = null
        instance = new RetrofitClient(); // Thread B: instance = null (chưa được update)
    }                                     // → Cả 2 tạo instance!
    return instance;
}
```
**Vấn đề**: Nhiều thread có thể tạo nhiều instance

**✓ ĐÚNG - Double-Checked Locking:**
```java
public static RetrofitClient getInstance() {
    if (instance == null) {                     // ✓ Check nhanh, không lock
        synchronized (RetrofitClient.class) {   // ✓ Chỉ lock khi cần
            if (instance == null) {              // ✓ Check lại sau khi lock
                instance = new RetrofitClient();
            }
        }
    }
    return instance;
}
```
**Kết quả**: Nhanh, thread-safe, chỉ tạo 1 instance

#### **Cách sử dụng:**

```java
// TRƯỚC:
ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

// SAU:
ApiService apiService = RetrofitClient.getInstance().getRetrofit().create(ApiService.class);
```

---

### 🔧 **Implementation 2: DatabaseSingleton**

#### **Code Implementation:**

```java
package me.etylix.lnread;

import android.content.Context;
import androidx.room.Room;

public class DatabaseSingleton {
    // 1. Singleton instance với volatile
    private static volatile DatabaseSingleton instance = null;
    
    // 2. AppDatabase object
    private AppDatabase database;
    
    // 3. PRIVATE CONSTRUCTOR
    private DatabaseSingleton(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(
                    context.getApplicationContext(),  // ✓ Dùng ApplicationContext
                    AppDatabase.class, 
                    "series-database"
            ).build();
        }
    }
    
    // 4. THREAD-SAFE getInstance
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
    
    // 5. Public method để lấy database
    public AppDatabase getDatabase() {
        return database;
    }
}
```

#### **Giải thích chi tiết:**

##### **a. Lấy ApplicationContext:**
```java
context.getApplicationContext()
```

**Tại sao dùng `getApplicationContext()` thay vì `context`?**

```java
// ❌ KHÔNG NÊN:
private DatabaseSingleton(Context context) {
    database = Room.databaseBuilder(context, ...).build();
    //                               ^^^^^^^ 
    // Nếu context là Activity context → Memory leak!
}
```

**Vấn đề:**
- Nếu dùng Activity context: Database giữ reference đến Activity
- Khi Activity bị destroy: Database không thể GC → **Memory leak**
- AppDatabase sống lâu hơn Activity → Dễ leak

```java
// ✓ ĐÚNG:
private DatabaseSingleton(Context context) {
    context.getApplicationContext()  // ✓ Application context sống lâu
    // Application context chỉ bị destroy khi app terminate
}
```

##### **b. Context parameter trong getInstance:**
```java
public static DatabaseSingleton getInstance(Context context) {
    if (instance == null) {
        instance = new DatabaseSingleton(context);  // Chỉ cần context lần đầu
    }
    return instance;
}
```

**Lưu ý**: Lần sau gọi có thể pass `null` vì đã có instance:
```java
// Lần đầu:
DatabaseSingleton.getInstance(context);

// Lần sau:
DatabaseSingleton.getInstance(null);  // OK, instance đã có
```

---

## 5. So sánh Trước và Sau

### 📊 **So sánh tổng quan:**

| Aspect | TRƯỚC | SAU (với Singleton) |
|--------|-------|---------------------|
| **Thread Safety** | ❌ Không đảm bảo | ✅ Đảm bảo |
| **Dependencies** | ❌ Tight coupling | ✅ Loose coupling |
| **Testability** | ❌ Khó test | ✅ Dễ test |
| **Memory Management** | ⚠️ Có thể leak | ✅ Safe |
| **Code Quality** | ⚠️ Trung bình | ✅ Clean |
| **Performance** | ⚠️ Có thể tạo nhiều instance | ✅ Hiệu quả |

---

### 🔍 **So sánh chi tiết từng file:**

#### **File: MainActivity.java**

##### **TRƯỚC:**
```java
public class MainActivity extends AppCompatActivity {
    private AppDatabase database;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // ❌ Tạo database instance ở đây
        database = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "series-database").build();
    }
    
    public AppDatabase getDatabase() {
        return database;
    }
}
```

##### **SAU:**
```java
public class MainActivity extends AppCompatActivity {
    private AppDatabase database;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // ✅ Sử dụng singleton
        database = DatabaseSingleton.getInstance(getApplicationContext())
                .getDatabase();
    }
    
    public AppDatabase getDatabase() {
        return database;
    }
}
```

---

#### **File: FavoritesFragment.java**

##### **TRƯỚC:**
```java
public class FavoritesFragment extends Fragment {
    private void loadFavorites() {
        new Thread(() -> {
            // ❌ Phải cast về MainActivity
            // ❌ Dependency quá chặt
            // ❌ Khó test (phải mock MainActivity)
            List<SeriesEntity> favorites = 
                ((MainActivity) getActivity())
                    .getDatabase()
                    .seriesDao()
                    .getAllSeries();
            
            getActivity().runOnUiThread(() -> {
                // Update UI...
            });
        }).start();
    }
}
```

**Nhược điểm:**
- Fragment phụ thuộc vào MainActivity
- Nếu MainActivity không phải là MainActivity → Crash
- Khó test unit test
- Không flexible

##### **SAU:**
```java
public class FavoritesFragment extends Fragment {
    private void loadFavorites() {
        new Thread(() -> {
            // ✅ Truy cập trực tiếp qua singleton
            // ✅ Không phụ thuộc Activity
            // ✅ Dễ test, dễ maintain
            List<SeriesEntity> favorites = 
                DatabaseSingleton.getInstance(getContext())
                    .getDatabase()
                    .seriesDao()
                    .getAllSeries();
            
            getActivity().runOnUiThread(() -> {
                // Update UI...
            });
        }).start();
    }
}
```

**Ưu điểm:**
- ✅ Không dependency vào Activity
- ✅ Dễ test (có thể mock DatabaseSingleton)
- ✅ Flexible hơn
- ✅ Code cleaner

---

#### **File: SeriesDetailFragment.java**

##### **TRƯỚC:**
```java
public class SeriesDetailFragment extends Fragment {
    private void onFavoriteButtonClick() {
        new Thread(() -> {
            // ❌ Lặp lại code, khó maintain
            SeriesEntity existing = ((MainActivity) getActivity())
                .getDatabase().seriesDao().getSeriesByName(name);
            
            if (existing == null) {
                ((MainActivity) getActivity()).getDatabase()
                    .seriesDao().insert(seriesEntity);
            } else {
                ((MainActivity) getActivity()).getDatabase()
                    .seriesDao().deleteBySeriesName(name);
            }
        }).start();
    }
    
    private void checkFavorite() {
        new Thread(() -> {
            // ❌ Lặp lại ((MainActivity) getActivity()).getDatabase()
            SeriesEntity existing = ((MainActivity) getActivity())
                .getDatabase().seriesDao().getSeriesByName(name);
            // ...
        }).start();
    }
}
```

##### **SAU:**
```java
public class SeriesDetailFragment extends Fragment {
    private void onFavoriteButtonClick() {
        new Thread(() -> {
            // ✅ Clean code, dễ đọc
            AppDatabase db = DatabaseSingleton.getInstance(getContext())
                .getDatabase();
            
            SeriesEntity existing = db.seriesDao().getSeriesByName(name);
            
            if (existing == null) {
                db.seriesDao().insert(seriesEntity);
            } else {
                db.seriesDao().deleteBySeriesName(name);
            }
        }).start();
    }
    
    private void checkFavorite() {
        new Thread(() -> {
            // ✅ Dùng lại database instance
            AppDatabase db = DatabaseSingleton.getInstance(getContext())
                .getDatabase();
            SeriesEntity existing = db.seriesDao().getSeriesByName(name);
            // ...
        }).start();
    }
}
```

---

## 6. Lợi ích đạt được

### 🎯 **1. Thread Safety**
```java
// Thread-safe đảm bảo chỉ 1 instance duy nhất
Thread thread1 = new Thread(() -> {
    RetrofitClient instance1 = RetrofitClient.getInstance();
});

Thread thread2 = new Thread(() -> {
    RetrofitClient instance2 = RetrofitClient.getInstance();
});

// instance1 == instance2 ✓ (cùng một object)
```

---

### 🎯 **2. Memory Efficiency**
```java
// TRƯỚC: Có thể tạo nhiều database instances
Room.databaseBuilder(...).build();  // Instance 1
Room.databaseBuilder(...).build();  // Instance 2
Room.databaseBuilder(...).build();  // Instance 3
// → 3 database instances = WASTE!

// SAU: Chỉ 1 instance
DatabaseSingleton.getInstance(context);  // Instance duy nhất
DatabaseSingleton.getInstance(context);  // Reuse instance
DatabaseSingleton.getInstance(context);  // Reuse instance
// → 1 database instance = EFFICIENT!
```

---

### 🎯 **3. Better Architecture**
```java
// TRƯỚC: Tight coupling
Fragment → MainActivity → Database
// Fragment phụ thuộc cứng vào MainActivity

// SAU: Loose coupling
Fragment → DatabaseSingleton → Database
// Fragment độc lập với MainActivity
```

---

### 🎯 **4. Easier Testing**
```java
// TRƯỚC: Khó test
public class FavoritesFragmentTest {
    @Test
    void testLoadFavorites() {
        // ❌ Phải mock MainActivity, getActivity(), getDatabase()
        // ❌ Phức tạp và khó maintain
    }
}

// SAU: Dễ test
public class FavoritesFragmentTest {
    @Test
    void testLoadFavorites() {
        // ✅ Chỉ cần mock DatabaseSingleton
        // ✅ Mock đơn giản hơn nhiều
    }
}
```

---

## 7. Thread Safety

### ⚠️ **Vấn đề Thread Safety trong Android:**

Android app chạy trong môi trường **multi-thread**:
- Main thread: Update UI
- Background thread: Network calls, database operations
- Worker threads: Image loading, file I/O

### 🔒 **Race Condition Example:**

```java
// ❌ KHÔNG THREAD-SAFE:
public class RetrofitClient {
    private static Retrofit retrofit = null;
    
    public static Retrofit getInstance() {
        if (retrofit == null) {              // Thread A: retrofit = null ✓
            retrofit = new Retrofit.Builder() // Thread B: retrofit = null ✓
                .build();                      // Cả 2 tạo instance!
        }                                     // → 2 instances được tạo!
        return retrofit;
    }
}
```

**Race Condition:**
```
Time    Thread A                        Thread B
0       if (retrofit == null)           -
1       (retrofit = null)               if (retrofit == null)
2       retrofit = new Retrofit()       (retrofit = null)
3       return retrofit                 retrofit = new Retrofit()
4       -                               return retrofit
```

**Kết quả**: 2 instances được tạo → Không phải Singleton!

---

### ✅ **Solution: Double-Checked Locking**

```java
public class RetrofitClient {
    private static volatile RetrofitClient instance = null;  // ✓ volatile
    
    public static RetrofitClient getInstance() {
        if (instance == null) {                              // ✓ Check 1
            synchronized (RetrofitClient.class) {           // ✓ Lock
                if (instance == null) {                      // ✓ Check 2
                    instance = new RetrofitClient();         // ✓ Tạo instance
                }
            }
        }
        return instance;
    }
}
```

**Vì sao cần `volatile`?**
```java
private static volatile RetrofitClient instance = null;
                                // ^^^^^^^
```

**Không có `volatile`:**
```java
// Thread A:                     Thread B:
instance = new RetrofitClient(); // Write to local cache
                                if (instance == null) {
                                 // Read old value (null)!
                                }
```

**Có `volatile`:**
```java
// Thread A:                     Thread B:
instance = new RetrofitClient(); // Write to main memory
                                 if (instance == null) {
                                 // Read from main memory ✓
                                 }
```

---

## 8. Best Practices

### ✅ **DO (Nên làm):**

#### **1. Dùng volatile cho instance:**
```java
private static volatile DatabaseSingleton instance = null;
```

#### **2. Dùng ApplicationContext cho Context:**
```java
private DatabaseSingleton(Context context) {
    database = Room.databaseBuilder(
        context.getApplicationContext(),  // ✓ Dùng ApplicationContext
        // ... không dùng Activity context
    ).build();
}
```

#### **3. Dùng Double-Checked Locking:**
```java
public static DatabaseSingleton getInstance(Context context) {
    if (instance == null) {
        synchronized (DatabaseSingleton.class) {
            if (instance == null) {  // ✓ Check lại
                instance = new DatabaseSingleton(context);
            }
        }
    }
    return instance;
}
```

#### **4. Private Constructor:**
```java
private DatabaseSingleton(Context context) {  // ✓ Private
    // ...
}
```

---

### ❌ **DON'T (Không nên làm):**

#### **1. Không dùng Activity Context:**
```java
// ❌ WRONG:
database = Room.databaseBuilder(
    context,  // Activity context → Memory leak!
    ...
).build();
```

#### **2. Không bỏ qua synchronized:**
```java
// ❌ WRONG - Không thread-safe:
public static DatabaseSingleton getInstance() {
    if (instance == null) {
        instance = new DatabaseSingleton();  // Race condition!
    }
    return instance;
}
```

#### **3. Không tạo nhiều constructors:**
```java
// ❌ WRONG:
public DatabaseSingleton() { ... }
public DatabaseSingleton(String config) { ... }  // Vi phạm Singleton
```

#### **4. Không clone instance:**
```java
// ❌ Nếu DatabaseSingleton implements Clonable:
DatabaseSingleton instance1 = DatabaseSingleton.getInstance();
DatabaseSingleton instance2 = instance1.clone();  // → 2 instances!
```

**Fix:**
```java
@Override
protected Object clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException();  // ✓ Prevent cloning
}
```

---

## 📚 **Tổng kết**

### **Những gì đã làm:**
1. ✅ Refactor `RetrofitClient` thành Singleton với thread-safe
2. ✅ Tạo `DatabaseSingleton` class mới
3. ✅ Update tất cả Fragment để dùng Singleton
4. ✅ Update `MainActivity` để dùng `DatabaseSingleton`
5. ✅ Loại bỏ dependency giữa Fragment và Activity

### **Kết quả:**
- ✅ **Thread-safe**: An toàn trong môi trường multi-thread
- ✅ **Memory-efficient**: Chỉ 1 instance cho mỗi resource
- ✅ **Clean code**: Dễ đọc, dễ maintain
- ✅ **Testable**: Dễ test với mock objects
- ✅ **Scalable**: Dễ mở rộng và refactor

### **Số lượng file đã sửa:**
- **Files mới**: 1 (`DatabaseSingleton.java`)
- **Files sửa**: 8 files (RetrofitClient + 5 Fragments + MainActivity)
- **Files helper**: 4 (`README.md`, `QUICK_START.md`, scripts)

---

## 📖 **Tài liệu tham khảo**

- [Singleton Pattern - GeeksforGeeks](https://www.geeksforgeeks.org/singleton-class-java/)
- [Double-Checked Locking - Wikipedia](https://en.wikipedia.org/wiki/Double-checked_locking)
- [Thread Safety in Android - Android Developers](https://developer.android.com/guide/background)
- [Room Database Best Practices](https://developer.android.com/training/data-storage/room)

---

**Ngày tạo:** 2025  
**Dự án:** LNRead - Light Novel Reader App

