package com.csstj.nurtree.data.model;

public class MinCircleInfo {
    //总分钟数
    private int totalmins;
    //剩余时间
    private int minlist;
    //使用完成进度
    private int progress;

    public MinCircleInfo(int totalmins,int minlist,int progress){
        this.totalmins = totalmins;
        this.minlist = minlist;
        this.progress = progress;
    }

    public int getTotalmins() {
        return totalmins;
    }

    public void setTotalmins(int totalmins) {
        this.totalmins = totalmins;
    }

    public int getMinlist() {
        return minlist;
    }

    public void setMinlist(int minlist) {
        this.minlist = minlist;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
