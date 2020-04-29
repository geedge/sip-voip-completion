package cn.ac.iie.bean.voipSipCount;

public class VoipResStat {
    private String res_stat;
    private String cseq;
    private long count;
    private String interval_time;

    public String getCseq() {
        return cseq;
    }

    public void setCseq(String cseq) {
        this.cseq = cseq;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getInterval_time() {
        return interval_time;
    }

    public void setInterval_time(String interval_time) {
        this.interval_time = interval_time;
    }

    public String getRes_stat() {
        return res_stat;
    }

    public void setRes_stat(String res_stat) {
        this.res_stat = res_stat;
    }
}
