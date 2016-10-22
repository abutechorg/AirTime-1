package simplebeans.transactionhistory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import simplebeans.SimpleStatusBean;

/**
 * Created by Owner on 8/20/2016.
 */
public class TransactionHistoryBean {
    @JsonProperty("time")
    private String date;
    @JsonProperty("amount")
    private long amount;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("status")
    private SimpleStatusBean status;
    @JsonIgnore
    private String searchChain;

    public TransactionHistoryBean(String date, long amount, String msisdn, SimpleStatusBean status, String searchChain) {
        this.setDate(date);
        this.setAmount(amount);
        this.setMsisdn(msisdn);
        this.setStatus(status);
        this.setSearchChain(searchChain);
    }

    public TransactionHistoryBean() {

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getSearchChain() {
        return searchChain;
    }

    public void setSearchChain(String searchChain) {
        this.searchChain = searchChain;
    }
}
