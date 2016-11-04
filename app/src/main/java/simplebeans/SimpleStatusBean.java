package simplebeans;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Owner on 7/10/2016.
 */
public class SimpleStatusBean {
    @JsonProperty("statusCode")
    private int statusCode;
    @JsonProperty("statusDesc")
    private String statusDesc;

    public SimpleStatusBean() {
    }

    public SimpleStatusBean(String message, int statusCode) {

        this.setMessage(message);
        this.setStatusCode(statusCode);
    }

    public String getMessage() {
        return statusDesc;
    }

    public void setMessage(String message) {
        this.statusDesc = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
