package simplebeans.topupbeans;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Owner on 7/19/2016.
 */
public class TopUpBean {
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("amount")
    private double amount;

    public TopUpBean(String msisdn, double amount) {
        this.setMsisdn(msisdn);
        this.setAmount(amount);
    }

    public TopUpBean() {

    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
