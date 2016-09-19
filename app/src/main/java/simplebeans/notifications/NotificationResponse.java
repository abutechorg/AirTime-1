package simplebeans.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import simplebeans.ResponseStatusSimpleBean;

/**
 * Created by Owner on 8/20/2016.
 */
public class NotificationResponse {
    @JsonProperty("notification")
    private
    List<NotificationBean> history;
    @JsonProperty("status")
    private
    ResponseStatusSimpleBean status;

    public NotificationResponse(List<NotificationBean> history, ResponseStatusSimpleBean status) {
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

    public ResponseStatusSimpleBean getStatus() {
        return status;
    }

    public void setStatus(ResponseStatusSimpleBean status) {
        this.status = status;
    }
}
