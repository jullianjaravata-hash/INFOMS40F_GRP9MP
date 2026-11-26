package com.example.javafx_test;

public class Champion {
    private int code;
    private String name;
    private String title;
    private String champClass;
    private String resource;
    private int hp;
    private int range;
    private String releaseDate;
    private boolean active;

    public Champion(int code, String name, String title, String champClass, String resource,
                    int hp, int range, String releaseDate, boolean active)
    {
        this.code = code;
        this.name = name;
        this.title = title;
        this.champClass = champClass;
        this.resource = resource;
        this.hp = hp;
        this.range = range;
        this.releaseDate = releaseDate;
        this.active = active;
    }

    public int getCode() { return code; }

    public String getName() { return name; }

    public String getTitle() { return title; }

    public String getChampClass() { return champClass; }

    public String getResource() { return resource; }

    public int getHp() { return hp; }

    public int getRange() { return range; }

    public String getReleaseDate() { return releaseDate; }

    public boolean isActive() { return active; }

}
