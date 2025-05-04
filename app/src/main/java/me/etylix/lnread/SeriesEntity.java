package me.etylix.lnread;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_series")
public class SeriesEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    public String url;
    private String seriesName;
    private String seriesImg;
    private String seriesAuthor;
    private String seriesPlot;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getURL() { return url; }
    public void setURL(String url) {this.url = url;}
    public String getSeriesName() { return seriesName; }
    public void setSeriesName(String seriesName) { this.seriesName = seriesName; }
    public String getSeriesImg() { return seriesImg; }
    public void setSeriesImg(String seriesImg) { this.seriesImg = seriesImg; }
    public String getSeriesAuthor() { return seriesAuthor; }
    public void setSeriesAuthor(String seriesAuthor) { this.seriesAuthor = seriesAuthor; }
    public String getSeriesPlot() { return seriesPlot; }
    public void setSeriesPlot(String seriesPlot) { this.seriesPlot = seriesPlot; }

}