package ru.Ivan;

public class Cache {
    private long time;
    private long start;
    private long finish;
    private String id;

    public Cache(long start, long end, String id) {
        this.start = start;
        this.finish = finish;
        this.id = id;
        this.time = System.currentTimeMillis();
    }
}
