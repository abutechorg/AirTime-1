package simplebeans.walletpayment;

import com.fasterxml.jackson.annotation.JsonProperty;

import simplebeans.SimpleStatusBean;

/**
 * Created by Hp on 10/7/2016.
 */
public class InitiateResponse {
    @JsonProperty("initUid")
    private String initUid;
    @JsonProperty("status")
    private SimpleStatusBean status;

    public InitiateResponse(String initUid, SimpleStatusBean status) {
        this.setInitUid(initUid);
        this.setStatus(status);
    }

    public InitiateResponse() {

    }

    public String getInitUid() {
        return initUid;
    }

    public void setInitUid(String initUid) {
        this.initUid = initUid;
    }

    public SimpleStatusBean getStatus() {
        return status;
    }

    public void setStatus(SimpleStatusBean status) {
        this.status = status;
    }
}
