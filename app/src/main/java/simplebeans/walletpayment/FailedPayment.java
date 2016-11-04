package simplebeans.walletpayment;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Hp on 10/10/2016.
 */
public class FailedPayment {
    @JsonProperty("initUid")
    private String initUid;
    @JsonProperty("status")
    private int status;
    @JsonProperty("message")
    private String message;

    public FailedPayment() {
    }

    public FailedPayment(String initUid, int status, String message) {
        this.initUid = initUid;
        this.status = status;
        this.message = message;
    }

    /**
     * @return the initUid
     */
    public String getInitUid() {
        return initUid;
    }

    /**
     * @param initUid the initUid to set
     */
    public void setInitUid(String initUid) {
        this.initUid = initUid;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
