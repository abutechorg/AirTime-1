package simplebeans.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;

import simplebeans.SimpleStatusBean;

/**
 * Created by Owner on 8/20/2016.
 */
public class NotificationBean {
    @JsonProperty("date")
    private String date;
    @JsonProperty("message")
    private String message;
    @JsonProperty("status")
    private SimpleStatusBean status;

    public NotificationBean(String date, String message, SimpleStatusBean status) {
        this.setDate(date);
        this.setMessage(message);
        this.setStatus(status);
    }

    public NotificationBean() {

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SimpleStatusBean getStatus() {
        return status;
    }

    public void setStatus(SimpleStatusBean status) {
        this.status = status;
    }
}
