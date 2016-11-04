package simplebeans.wallethistory;

import com.fasterxml.jackson.annotation.JsonProperty;

import simplebeans.SimpleStatusBean;

/**
 * Created by Owner on 8/20/2016.
 */
public class WalletHistoryBean {
    @JsonProperty("transactionId")
    private long transactionId;
    @JsonProperty("date")
    private String date;
    @JsonProperty("payType")
    private String payType;
    @JsonProperty("payName")
    private String payName;
    @JsonProperty("amount")
    private long amount;
    @JsonProperty("status")
    private SimpleStatusBean status;

    public WalletHistoryBean(long transactionId, String date, String payType, String payName, long amount, SimpleStatusBean status) {
        this.setTransactionId(transactionId);
        this.setDate(date);
        this.setPayType(payType);
        this.setPayName(payName);
        this.setAmount(amount);
        this.setStatus(status);
    }

    public WalletHistoryBean() {

    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayName() {
        return payName;
    }

    public void setPayName(String payName) {
        this.payName = payName;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public SimpleStatusBean getStatus() {
        return status;
    }

    public void setStatus(SimpleStatusBean status) {
        this.status = status;
    }
}
