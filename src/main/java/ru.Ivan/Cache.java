package ru.Ivan;

import org.zeromq.ZFrame;

public class Cache {
    private long time;
    private long start;
    private long finish;
    private String id;
    private ZFrame frame;

    public Cache(long start, long finish, String id, ZFrame frame) {
        this.start = start;
        this.finish = finish;
        this.id = id;
        this.frame = frame;
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
