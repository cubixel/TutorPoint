package model;

public class Subject {
    private int id;
    private String name;
    private String thumbnailPath;

    public Subject(int id, String name, String thumbnailPath) {
        this.id = id;
        this.name = name;
        this.thumbnailPath = thumbnailPath;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }
}
