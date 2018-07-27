package com.icomm_semi.xuan.babystore;

public class AudioObject {
    private String name;
    private String duration;
    private String cur_pts;

    public void setName(String name) {
        this.name = name;
    }

    public void setCur_pts(String cur_pts) {
        this.cur_pts = cur_pts;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public String getCur_pts() {
        return cur_pts;
    }

    public String getDuration() {
        return duration;
    }
}
