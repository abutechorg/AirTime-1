package simplebeans.balancebeans;

import com.fasterxml.jackson.annotation.JsonProperty;

import simplebeans.SimpleStatusBean;

/**
 * Created by Owner on 7/18/2016.
 */
public class BalanceResponse {
    @JsonProperty("balance")
    private String  balance;
    @JsonProperty("lastTxTime")
    private String  lastTxTime;
    @JsonProperty("status")
    private SimpleStatusBean status;

    public BalanceResponse(String balance, String lastTxTime, SimpleStatusBean status) {
        this.setBalance(balance);
        this.setLastTxTime(lastTxTime);
        this.setStatus(status);
    }

    public BalanceResponse() {

    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getLastTxTime() {
        return lastTxTime;
    }

    public void setLastTxTime(String lastTxTime) {
        this.lastTxTime = lastTxTime;
    }

    public SimpleStatusBean getStatus() {
        return status;
    }

    public void setStatus(SimpleStatusBean status) {
        this.status = status;
    }
}
