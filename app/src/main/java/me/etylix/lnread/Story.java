package me.etylix.lnread;

public class Story {
    String name;
    String author;
    String img;

    public Story(String name, String author, String img) {
        this.name = name;
        this.author = author;
        this.img = img;
    }

    public Story() {

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getImg() {
        return img;
    }
}
