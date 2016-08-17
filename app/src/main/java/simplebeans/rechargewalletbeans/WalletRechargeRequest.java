package simplebeans.rechargewalletbeans;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Owner on 7/28/2016.
 */
public class WalletRechargeRequest {
    @JsonProperty("amount")
    private double amount;
    @JsonProperty("destRefId")
    private String msisdn;
    @JsonProperty("destType")
    private long destType;
    @JsonProperty("id")
    private long id;
    @JsonProperty("newBalance")
    private long newBalance;
    @JsonProperty("prevBalance")
    private long prevBalance;
    @JsonProperty("sourceType")
    private long sourceType;
    @JsonProperty("srcrefId")
    private String srcrefId;
    @JsonProperty("transTime")
    private String transTime;
    @JsonProperty("transTypeId")
    private long transTypeId;

    public WalletRechargeRequest(double amount, String msisdn, long destType, long id, long newBalance, long prevBalance, long sourceType, String srcrefId, String transTime, long transTypeId) {
        this.setAmount(amount);
        this.setMsisdn(msisdn);
        this.setDestType(destType);
        this.setId(id);
        this.setNewBalance(newBalance);
        this.setPrevBalance(prevBalance);
        this.setSourceType(sourceType);
        this.setSrcrefId(srcrefId);
        this.setTransTime(transTime);
        this.setTransTypeId(transTypeId);
    }

    public WalletRechargeRequest() {

    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public long getDestType() {
        return destType;
    }

    public void setDestType(long destType) {
        this.destType = destType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(long newBalance) {
        this.newBalance = newBalance;
    }

    public long getPrevBalance() {
        return prevBalance;
    }

    public void setPrevBalance(long prevBalance) {
        this.prevBalance = prevBalance;
    }

    public long getSourceType() {
        return sourceType;
    }

    public void setSourceType(long sourceType) {
        this.sourceType = sourceType;
    }

    public String getSrcrefId() {
        return srcrefId;
    }

    public void setSrcrefId(String srcrefId) {
        this.srcrefId = srcrefId;
    }

    public String getTransTime() {
        return transTime;
    }

    public void setTransTime(String transTime) {
        this.transTime = transTime;
    }

    public long getTransTypeId() {
        return transTypeId;
    }

    public void setTransTypeId(long transTypeId) {
        this.transTypeId = transTypeId;
    }
}
