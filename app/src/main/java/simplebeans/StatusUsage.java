package simplebeans;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Hp on 9/19/2016.
 */
public class StatusUsage {
    @JsonProperty("status")
    private
    SimpleStatusBean status;

    public StatusUsage(SimpleStatusBean status) {
        this.setStatus(status);
    }

    public StatusUsage() {

    }

    public SimpleStatusBean getStatus() {
        return status;
    }

    public void setStatus(SimpleStatusBean status) {
        this.status = status;
    }
}
