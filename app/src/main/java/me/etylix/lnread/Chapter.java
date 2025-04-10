package me.etylix.lnread;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Chapter implements Serializable {
    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private String id;

    @SerializedName("url")
    private String url;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getURL() { return url; }
    public void setURL(String id) { this.id = url; }
}
