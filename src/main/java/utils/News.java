package utils;

public class News {
    private String id;
    private String category;
    private String headline;
    private String authors;
    private String link;
    private String short_description;
    private String date;
    private int views;
    private int likes;

    public News() {

    }

    public News(String id, String headline, String category, String authors, String link, String short_description, String date, int views, int likes) {
        this();

        this.id = id;
        this.category = category;
        this.headline = headline;
        this.authors = authors;
        this.link = link;
        this.short_description = short_description;
        this.date = date;
        this.views = views;
        this.likes = likes;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public String getHeadline() {
        return this.headline;
    }

    public String getAuthors() {
        return authors;
    }

    public String getLink() {
        return link;
    }

    public String getShort_description() {
        return short_description;
    }

    public String getDate() {
        return date;
    }

    public int getViews() {
        return views;
    }

    public int getLikes() {
        return likes;
    }

    public String getCategory() {
        return category;
    }
}
