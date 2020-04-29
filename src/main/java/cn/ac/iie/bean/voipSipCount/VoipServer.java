package cn.ac.iie.bean.voipSipCount;

public class VoipServer {
    private String server;
    private long count;
    private String interval_time;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
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
}
