package simplebeans.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import simplebeans.SimpleStatusBean;

/**
 * Created by Owner on 8/20/2016.
 */
public class NotificationResponse {
    @JsonProperty("notification")
    private
    List<NotificationBean> history;
    @JsonProperty("status")
    private
    SimpleStatusBean status;

    public NotificationResponse(List<NotificationBean> history, SimpleStatusBean status) {
        this.setHistory(history);
        this.setStatus(status);
    }

    public NotificationResponse() {

    }

    public List<NotificationBean> getHistory() {
        return history;
    }

    public void setHistory(List<NotificationBean> history) {
        this.history = history;
    }

    public SimpleStatusBean getStatus() {
        return status;
    }

    public void setStatus(SimpleStatusBean status) {
        this.status = status;
    }
}
