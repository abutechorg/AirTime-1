package simplebeans.topupbeans;

import com.fasterxml.jackson.annotation.JsonProperty;

import simplebeans.SimpleStatusBean;

/**
 * Created by Hp on 10/12/2016.
 */
public class TopUpResponseBean {
    @JsonProperty("amount")
    private
    long amount;
    @JsonProperty("msisdn")
    private
    String msisdn;
    @JsonProperty("status")
    private
    SimpleStatusBean status;

    public TopUpResponseBean(long amount, String msisdn, SimpleStatusBean status) {
        this.setAmount(amount);
        this.setMsisdn(msisdn);
        this.setStatus(status);
    }

    public TopUpResponseBean() {

    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public SimpleStatusBean getStatus() {
        return status;
    }

    public void setStatus(SimpleStatusBean status) {
        this.status = status;
    }
}
