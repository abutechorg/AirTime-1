package simplebeans.favorite;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Owner on 8/19/2016.
 */
public class FavoriteResponseBean {
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("amount")
    private long amount;

    public FavoriteResponseBean(String msisdn, long amount) {
        this.setMsisdn(msisdn);
        this.setAmount(amount);
    }

    public FavoriteResponseBean() {

    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
