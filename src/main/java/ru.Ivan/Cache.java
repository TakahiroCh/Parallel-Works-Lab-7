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

    public long getFinish() {
        return finish;
    }

    public long getStart() {
        return start;
    }

    public long getTime() {
        return time;
    }

    public String getId() {
        return id;
    }


}
