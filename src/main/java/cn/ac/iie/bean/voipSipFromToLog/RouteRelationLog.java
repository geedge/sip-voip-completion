package cn.ac.iie.bean.voipSipFromToLog;

/**
 * voip路由关系表
 */
public class RouteRelationLog {
    private long timestamp;
    private String from_domain;
    private String to_domain;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFrom_domain() {
        return from_domain;
    }

    public void setFrom_domain(String from_domain) {
        this.from_domain = from_domain;
    }

    public String getTo_domain() {
        return to_domain;
    }

    public void setTo_domain(String to_domain) {
        this.to_domain = to_domain;
    }
}
