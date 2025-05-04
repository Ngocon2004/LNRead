package me.etylix.lnread;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Series implements Serializable {
    @SerializedName("series-name")
    private String seriesName;

    @SerializedName("series-img")
    private String seriesImg;

    @SerializedName("series-author")
    private String seriesAuthor;

    @SerializedName("series-plot")
    private String seriesPlot;

    @SerializedName("series-genre")
    private List<String> seriesGenre;

    @SerializedName("series-chapter")
    private List<Chapter> seriesChapter;

    public String getSeriesName() { return seriesName; }
    public void setSeriesName(String seriesName) { this.seriesName = seriesName; }
    public String getSeriesImg() { return seriesImg; }
    public void setSeriesImg(String seriesImg) { this.seriesImg = seriesImg; }
    public String getSeriesAuthor() { return seriesAuthor; }
    public void setSeriesAuthor(String seriesAuthor) { this.seriesAuthor = seriesAuthor; }
    public String getSeriesPlot() { return seriesPlot; }
    public void setSeriesPlot(String seriesPlot) { this.seriesPlot = seriesPlot; }
    public List<String> getSeriesGenre() { return seriesGenre; }
    public void setSeriesGenre(List<String> seriesGenre) { this.seriesGenre = seriesGenre; }
    public List<Chapter> getSeriesChapter() { return seriesChapter; }
    public void setSeriesChapter(List<Chapter> seriesChapter) { this.seriesChapter = seriesChapter; }
}
